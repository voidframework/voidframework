package dev.voidframework.web.http;

import com.google.inject.Injector;
import dev.voidframework.core.conversion.Conversion;
import dev.voidframework.web.exception.HttpException;
import dev.voidframework.web.http.param.RequestBody;
import dev.voidframework.web.http.param.RequestPath;
import dev.voidframework.web.http.param.RequestVariable;
import dev.voidframework.web.routing.ResolvedRoute;
import dev.voidframework.web.routing.Router;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Http request handler.
 */
public final class HttpRequestHandler {

    private static final Map<Class<?>, PrimitiveAlternative> PRIMITIVE_ALTERNATIVE_MAP = new HashMap<>() {{
        put(boolean.class, new PrimitiveAlternative(Boolean.class, false));
        put(byte.class, new PrimitiveAlternative(Byte.class, 0));
        put(char.class, new PrimitiveAlternative(Character.class, 0));
        put(double.class, new PrimitiveAlternative(Double.class, 0d));
        put(float.class, new PrimitiveAlternative(Float.class, 0f));
        put(int.class, new PrimitiveAlternative(Integer.class, 0));
        put(long.class, new PrimitiveAlternative(Long.class, 0));
        put(short.class, new PrimitiveAlternative(Short.class, 0));
    }};

    private final Conversion conversion;
    private final Injector injector;
    private final Router router;
    private final ErrorHandler errorHandler;

    /**
     * Build a new instance.
     *
     * @param injector     The injector instance
     * @param errorHandler The error handler to use
     */
    public HttpRequestHandler(final Injector injector,
                              final ErrorHandler errorHandler) {

        this.injector = injector;
        this.errorHandler = errorHandler;
        this.conversion = this.injector.getInstance(Conversion.class);
        this.router = this.injector.getInstance(Router.class);
    }

    /**
     * This method is called each time a bad request occur.
     *
     * @param context The current context
     * @param cause   The cause (OPTIONAL)
     * @return A result
     */
    public Result onBadRequest(final Context context, final HttpException.BadRequest cause) {
        return errorHandler.onBadRequest(context, cause);
    }

    /**
     * This method is called each time the framework need to route a request.
     *
     * @param context The current context
     * @return A result
     */
    public Result onRouteRequest(final Context context) {

        final ResolvedRoute resolvedRoute = router.resolveRoute(context.getRequest().getHttpMethod(), context.getRequest().getRequestURI());
        if (resolvedRoute == null) {
            return errorHandler.onNotFound(context, null);
        }

        try {
            final Object controllerInstance = this.injector.getInstance(resolvedRoute.controllerClassType());

            if (resolvedRoute.method().getParameterCount() == 0) {
                // No parameters, just invoke the controller method
                return (Result) resolvedRoute.method().invoke(controllerInstance);
            } else {
                // Method have some parameter(s)
                final Object[] methodArgumentValueArray = this.buildMethodArguments(context, resolvedRoute);
                return (Result) resolvedRoute.method().invoke(controllerInstance, methodArgumentValueArray);
            }
        } catch (final Throwable throwable) {
            final Throwable cause = throwable.getCause() == null ? throwable : throwable.getCause();
            if (cause instanceof HttpException.NotFound) {
                return errorHandler.onNotFound(context, (HttpException.NotFound) cause);
            } else if (cause instanceof HttpException.BadRequest) {
                return errorHandler.onBadRequest(context, (HttpException.BadRequest) cause);
            }

            return errorHandler.onServerError(context, cause);
        }
    }

    /**
     * Builds method arguments.
     *
     * @param context The current context
     * @param resolvedRoute The resolved route
     * @return An array containing method arguments
     */
    private Object[] buildMethodArguments(final Context context, final ResolvedRoute resolvedRoute) {
        int idx = 0;
        final Object[] methodArgumentValueArray = new Object[resolvedRoute.method().getParameterCount()];

        for (final Parameter parameter : resolvedRoute.method().getParameters()) {
            if (parameter.getType().isAssignableFrom(Context.class)) {
                methodArgumentValueArray[idx] = context;
                idx += 1;
                continue;
            }

            final RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
            final RequestPath requestPath = parameter.getAnnotation(RequestPath.class);
            final RequestVariable requestVariable = parameter.getAnnotation(RequestVariable.class);

            if (requestBody != null) {
                if (context.getRequest().getBodyContent().contentType() != null) {
                    methodArgumentValueArray[idx] = switch (context.getRequest().getBodyContent().contentType()) {
                        case HttpContentType.APPLICATION_JSON -> context.getRequest().getBodyContent().asJson(parameter.getType());
                        case HttpContentType.MULTIPART_FORM_DATA -> context.getRequest().getBodyContent().asFormData(parameter.getType());
                        case HttpContentType.TEXT_YAML -> context.getRequest().getBodyContent().asYaml(parameter.getType());
                        default -> throw new HttpException.BadRequest("Unhandled body content");
                    };
                } else {
                    methodArgumentValueArray[idx] = null;
                }
            } else if (requestPath != null) {
                methodArgumentValueArray[idx] = convertValueToParameterType(
                    resolvedRoute.extractedParameterValues().getOrDefault(requestPath.value(), null),
                    parameter.getType());
            } else if (requestVariable != null) {
                methodArgumentValueArray[idx] = convertValueToParameterType(
                    context.getRequest().getQueryStringParameter(requestVariable.value()),
                    parameter.getType());
            } else {
                methodArgumentValueArray[idx] = this.injector.getInstance(parameter.getType());
            }

            idx += 1;
        }

        return methodArgumentValueArray;
    }

    /**
     * Try to convert value from a String into the needed parameter type.
     *
     * @param value              The string containing the value to convert
     * @param parameterTypeClass The needed output parameter type class
     * @return The converter value, otherwise, null
     */
    private Object convertValueToParameterType(final String value, final Class<?> parameterTypeClass) {
        Class<?> clazzToUse = parameterTypeClass;
        Object defaultValue = null;

        if (parameterTypeClass == String.class) {
            return value;
        }

        final PrimitiveAlternative primitiveAlternative = PRIMITIVE_ALTERNATIVE_MAP.get(parameterTypeClass);
        if (primitiveAlternative != null) {
            clazzToUse = primitiveAlternative.replacementClass;
            defaultValue = primitiveAlternative.defaultValue;
        }

        final Object converterValue = conversion.convert(value, clazzToUse);
        return converterValue != null ? converterValue : defaultValue;
    }

    /**
     * Defines an alternative for primitive value conversion.
     *
     * @param replacementClass The remplacement class
     * @param defaultValue     The default value if converter return {@code null}
     */
    private record PrimitiveAlternative(Class<?> replacementClass, Object defaultValue) {
    }
}
