package com.voidframework.core.routing.impl;

import com.voidframework.core.routing.HttpMethod;
import com.voidframework.core.routing.RouteBuilder;

public class DefaultRouteBuilder implements RouteBuilder {

    private HttpMethod httpMethod;
    private String route;
    private Class<?> controllerClass;
    private String methodName;

    /**
     * Build a new instance.
     */
    DefaultRouteBuilder() {
    }

    @Override
    public RouteBuilder method(final HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    @Override
    public RouteBuilder route(final String route) {
        this.route = route;
        return this;
    }

    @Override
    public RouteBuilder call(final Class<?> controllerClass, final String methodName) {
        this.controllerClass = controllerClass;
        this.methodName = methodName;
        return this;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRoute() {
        return route;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public String getMethodName() {
        return methodName;
    }
}
