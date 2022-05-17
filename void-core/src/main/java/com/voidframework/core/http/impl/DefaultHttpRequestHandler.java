package com.voidframework.core.http.impl;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.voidframework.core.conversion.Conversion;
import com.voidframework.core.http.Context;
import com.voidframework.core.http.HttpRequest;
import com.voidframework.core.http.HttpRequestHandler;
import com.voidframework.core.http.RequestPath;
import com.voidframework.core.http.RequestVariable;
import com.voidframework.core.http.Result;
import com.voidframework.core.routing.ResolvedRoute;
import com.voidframework.core.routing.Router;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link HttpRequestHandler}.
 */
public class DefaultHttpRequestHandler implements HttpRequestHandler {

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

    /**
     * Build a new instance.
     *
     * @param router The current router instance
     */
    @Inject
    private DefaultHttpRequestHandler(final Conversion conversion,
                                      final Injector injector,
                                      final Router router) {
        this.conversion = conversion;
        this.injector = injector;
        this.router = router;
    }

    @Override
    public Result onRouteRequest(final HttpRequest httpRequest) {

        final ResolvedRoute resolvedRoute = router.resolveRoute(httpRequest.getHttpMethod(), httpRequest.getRequestURI());
        if (resolvedRoute == null) {
            return Result.notFound("404 Not Found");
        }

        // Build Context
        final Context context = new DefaultContext(httpRequest);

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
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
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
