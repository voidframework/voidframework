package com.voidframework.core.http.impl;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.voidframework.core.http.Context;
import com.voidframework.core.http.HttpRequestHandler;
import com.voidframework.core.routing.Route;
import com.voidframework.core.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Default implementation of the HTTP request handler.
 */
public class DefaultHttpRequestHandler implements HttpRequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final Injector injector;
    private final Router router;

    /**
     * Build a new instance.
     *
     * @param router The current router instance
     */
    @Inject
    private DefaultHttpRequestHandler(final Injector injector, final Router router) {
        this.injector = injector;
        this.router = router;
    }

    @Override
    public String onRouteRequest(final Context context) {

        final Optional<Route> routeOptional = router.resolveRoute(context.getHttpMethod(), context.getRequestURI());

        return routeOptional.map(route -> {
            try {
                if (route.method.getParameterCount() == 0) {
                    return (String) route.method.invoke(injector.getInstance(route.controllerClass));
                } else {
                    final Object[] methodArgumentValueArray = new Object[route.method.getParameterCount()];
                    methodArgumentValueArray[0] = "John";

                    return (String) route.method.invoke(injector.getInstance(route.controllerClass), methodArgumentValueArray);
                }
            } catch (final IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }).orElse("404 Not Found");
    }
}
