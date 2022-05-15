package com.voidframework.core.http.impl;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.voidframework.core.conversion.Conversion;
import com.voidframework.core.http.Context;
import com.voidframework.core.http.HttpRequestHandler;
import com.voidframework.core.http.RequestPath;
import com.voidframework.core.http.RequestVariable;
import com.voidframework.core.routing.ResolvedRoute;
import com.voidframework.core.routing.Router;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * Default implementation of the HTTP request handler.
 */
public class DefaultHttpRequestHandler implements HttpRequestHandler {

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
    public String onRouteRequest(final Context context) {

        final Optional<ResolvedRoute> routeOptional = router.resolveRoute(context.getHttpMethod(), context.getRequestURI());

        return routeOptional.map(route -> {
            try {
                if (route.method().getParameterCount() == 0) {
                    // No parameters, just invoke the controller method
                    return (String) route.method().invoke(injector.getInstance(route.controllerClass()));
                } else {
                    // Method have some parameter(s)
                    final Object[] methodArgumentValueArray = new Object[route.method().getParameterCount()];
                    int idx = 0;
                    for (final Parameter parameter : route.method().getParameters()) {
                        final RequestPath requestPath = parameter.getAnnotation(RequestPath.class);
                        final RequestVariable requestVariable = parameter.getAnnotation(RequestVariable.class);

                        if (requestPath != null) {
                            methodArgumentValueArray[idx] = convertValueToParameterType(
                                route.extractedParameterValues().getOrDefault(requestPath.value(), null),
                                parameter.getType());
                        } else if (requestVariable != null) {
                            methodArgumentValueArray[idx] = convertValueToParameterType(
                                context.getQueryStringParameter(requestVariable.value()),
                                parameter.getType());
                        } else {
                            // TODO: Check for Session, Cookie, ... before using Injector
                            methodArgumentValueArray[idx] = this.injector.getInstance(parameter.getType());
                        }

                        idx += 1;
                    }

                    return (String) route.method().invoke(injector.getInstance(route.controllerClass()), methodArgumentValueArray);
                }
            } catch (final IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }).orElse("404 Not Found");
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

        if (parameterTypeClass == int.class) {
            clazzToUse = Integer.class;
            defaultValue = 0;
        } else if (parameterTypeClass == short.class) {
            clazzToUse = Short.class;
            defaultValue = 0;
        } else if (parameterTypeClass == long.class) {
            clazzToUse = Long.class;
            defaultValue = 0;
        } else if (parameterTypeClass == boolean.class) {
            clazzToUse = Boolean.class;
            defaultValue = false;
        } else if (parameterTypeClass == double.class || parameterTypeClass == float.class) {
            clazzToUse = Double.class;
            defaultValue = 0d;
        } else if (parameterTypeClass == byte.class) {
            clazzToUse = Byte.class;
            defaultValue = 0;
        } else if (parameterTypeClass == char.class) {
            clazzToUse = Character.class;
            defaultValue = 0;
        }

        final Object converterValue = conversion.convert(value, clazzToUse);
        return converterValue != null ? converterValue : defaultValue;
    }
}
