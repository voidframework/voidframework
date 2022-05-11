package com.voidframework.core.routing;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public final class Route {

    public final HttpMethod httpMethod;
    public final Pattern routePattern;
    public final Class<?> controllerClass;
    public final Method method;

    /**
     * Build a new instance.
     *
     * @param httpMethod      HTTP method
     * @param routePattern    URL regex pattern
     * @param controllerClass The controller class
     * @param method          The method
     */
    public Route(final HttpMethod httpMethod,
                 final Pattern routePattern,
                 final Class<?> controllerClass,
                 final Method method) {
        this.httpMethod = httpMethod;
        this.routePattern = routePattern;
        this.controllerClass = controllerClass;
        this.method = method;
    }

    public static final class MethodArgument {
        public final boolean isManagedByGuice;
        public final Class<?> argumentClass;

        public MethodArgument(final boolean isManagedByGuice, final Class<?> argumentClass) {
            this.isManagedByGuice = isManagedByGuice;
            this.argumentClass = argumentClass;
        }
    }
}
