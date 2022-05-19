package com.voidframework.web.http.impl;

import com.google.inject.Injector;
import com.voidframework.core.conversion.Conversion;
import com.voidframework.web.exception.HttpException;
import com.voidframework.web.http.Context;
import com.voidframework.web.http.ErrorHandler;
import com.voidframework.web.http.HttpRequest;
import com.voidframework.web.http.param.RequestPath;
import com.voidframework.web.http.param.RequestVariable;
import com.voidframework.web.http.Result;
import com.voidframework.web.routing.ResolvedRoute;
import com.voidframework.web.routing.Router;

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
     * @param injector           The current injector instance
     * @param errorHandler The current error handler to use
     */
    public HttpRequestHandler(final Injector injector,
                              final ErrorHandler errorHandler) {

        this.injector = injector;
        this.errorHandler = errorHandler;
        this.conversion = this.injector.getInstance(Conversion.class);
        this.router = this.injector.getInstance(Router.class);
    }

    public Result onRouteRequest(final HttpRequest httpRequest) {

        // Build Context
        final Context context = new DefaultContext(httpRequest);

        final ResolvedRoute resolvedRoute = router.resolveRoute(httpRequest.getHttpMethod(), httpRequest.getRequestURI());
        if (resolvedRoute == null) {
            return errorHandler.onNotFound(context, null);
        }

        try {
            if (resolvedRoute.method().getParameterCount() == 0) {
                // No parameters, just invoke the controller method
                return (Result) resolvedRoute.method().invoke(injector.getInstance(resolvedRoute.controllerClass()));
            } else {
                // Method have some parameter(s)
                final Object[] methodArgumentValueArray = new Object[resolvedRoute.method().getParameterCount()];
                int idx = 0;
                for (final Parameter parameter : resolvedRoute.method().getParameters()) {
                    if (parameter.getType().isAssignableFrom(Context.class)) {
                        methodArgumentValueArray[idx] = context;
                        idx += 1;
                        continue;
                    }

                    final RequestPath requestPath = parameter.getAnnotation(RequestPath.class);
                    final RequestVariable requestVariable = parameter.getAnnotation(RequestVariable.class);

                    if (requestPath != null) {
                        methodArgumentValueArray[idx] = convertValueToParameterType(
                            resolvedRoute.extractedParameterValues().getOrDefault(requestPath.value(), null),
                            parameter.getType());
                    } else if (requestVariable != null) {
                        methodArgumentValueArray[idx] = convertValueToParameterType(
                            httpRequest.getQueryStringParameter(requestVariable.value()),
                            parameter.getType());
                    } else {
                        methodArgumentValueArray[idx] = this.injector.getInstance(parameter.getType());
                    }

                    idx += 1;
                }

                return (Result) resolvedRoute.method().invoke(
                    injector.getInstance(resolvedRoute.controllerClass()), methodArgumentValueArray);
            }
        } catch (final Throwable throwable) {
            final Throwable cause = throwable.getCause();
            if (cause instanceof HttpException.NotFound) {
                return errorHandler.onNotFound(context, (HttpException.NotFound) cause);
            }

            return errorHandler.onServerError(context, cause);
        }
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
