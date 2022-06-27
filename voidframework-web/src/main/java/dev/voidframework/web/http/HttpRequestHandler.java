package dev.voidframework.web.http;

import com.google.inject.Injector;
import com.typesafe.config.Config;
import dev.voidframework.core.conversion.Conversion;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.web.exception.HttpException;
import dev.voidframework.web.filter.DefaultFilterChain;
import dev.voidframework.web.filter.Filter;
import dev.voidframework.web.filter.FilterChain;
import dev.voidframework.web.http.param.RequestBody;
import dev.voidframework.web.http.param.RequestPath;
import dev.voidframework.web.http.param.RequestVariable;
import dev.voidframework.web.routing.ResolvedRoute;
import dev.voidframework.web.routing.Router;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private final Injector injector;
    private final List<Class<? extends Filter>> globalFilterClassTypes;

    private final ErrorHandler errorHandler;
    private final Conversion conversion;
    private final Router router;
    private final Config configuration;
    private TemplateRenderer templateRenderer;

    /**
     * Build a new instance.
     *
     * @param injector               The injector instance
     * @param errorHandler           The error handler to use
     * @param globalFilterClassTypes The global filter class types
     */
    public HttpRequestHandler(final Injector injector,
                              final ErrorHandler errorHandler,
                              final List<Class<? extends Filter>> globalFilterClassTypes) {

        this.injector = injector;
        this.errorHandler = errorHandler;
        this.globalFilterClassTypes = globalFilterClassTypes;
        this.conversion = this.injector.getInstance(Conversion.class);
        this.router = this.injector.getInstance(Router.class);
        this.configuration = this.injector.getInstance(Config.class);
        try {
            this.templateRenderer = this.injector.getInstance(TemplateRenderer.class);
        } catch (final Exception ignore) {
            this.templateRenderer = null;
        }
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

        // The processing carried out here is based exclusively on the chaining of Filters, some
        // of which are conditioned according to whether the route is found. This way, global
        // filters will always be executed, even on error pages
        final List<Filter> filterList = new ArrayList<>();

        // Instantiates global filters
        for (final Class<? extends Filter> filterClassType : this.globalFilterClassTypes) {
            filterList.add(this.injector.getInstance(filterClassType));
        }

        // Tries to resolve route
        final ResolvedRoute resolvedRoute = router.resolveRoute(context.getRequest().getHttpMethod(), context.getRequest().getRequestURI());
        if (resolvedRoute == null) {
            // No route found, only the Filter showing the "404" error page is required
            final Filter callNotFoundFilter = (ctx, filterChain) -> {
                final Result result = errorHandler.onNotFound(ctx, null);
                result.getResultProcessor().process(ctx, configuration, templateRenderer);

                return result;
            };
            filterList.add(callNotFoundFilter);
        } else {
            // Instantiates controller and method filters
            for (final Class<? extends Filter> filterClassType : resolvedRoute.filterClassTypes()) {
                filterList.add(this.injector.getInstance(filterClassType));
            }

            // Instantiates the Filter in charge of calling the controller method with the
            // right arguments and handling possible errors
            final Filter callControllerFile = (ctx, filterChain) -> {
                final Object controllerInstance = injector.getInstance(resolvedRoute.controllerClassType());

                try {
                    final Result result;
                    if (resolvedRoute.method().getParameterCount() == 0) {
                        // No parameters, just invoke the controller method
                        result = (Result) resolvedRoute.method().invoke(controllerInstance);
                    } else {
                        // Method have some parameter(s)
                        final Object[] methodArgumentValueArray = buildMethodArguments(ctx, resolvedRoute);
                        result = (Result) resolvedRoute.method().invoke(controllerInstance, methodArgumentValueArray);
                    }

                    result.getResultProcessor().process(ctx, configuration, templateRenderer);
                    return result;

                } catch (final Throwable throwable) {
                    final Throwable cause = throwable.getCause() == null ? throwable : throwable.getCause();

                    final Result result;
                    if (cause instanceof HttpException.NotFound) {
                        result = errorHandler.onNotFound(ctx, (HttpException.NotFound) cause);
                    } else if (cause instanceof HttpException.BadRequest) {
                        result = errorHandler.onBadRequest(ctx, (HttpException.BadRequest) cause);
                    } else {
                        result = errorHandler.onServerError(ctx, throwable);
                    }

                    result.getResultProcessor().process(ctx, configuration, templateRenderer);

                    return result;
                }
            };

            filterList.add(callControllerFile);
        }

        try {
            // Process the entire Filters chain
            final FilterChain filterChain = new DefaultFilterChain(filterList);
            return filterChain.applyNext(context);
        } catch (final Throwable throwable) {
            //final Throwable cause = throwable.getCause() == null ? throwable : throwable.getCause();
            final Result result = errorHandler.onServerError(context, throwable);
            result.getResultProcessor().process(context, configuration, templateRenderer);
            return result;
        }
    }

    /**
     * Builds method arguments.
     *
     * @param context       The current context
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
                        case HttpContentType.APPLICATION_X_FORM_URLENCODED, HttpContentType.MULTIPART_FORM_DATA ->
                            context.getRequest().getBodyContent().asFormData(parameter.getType());
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
