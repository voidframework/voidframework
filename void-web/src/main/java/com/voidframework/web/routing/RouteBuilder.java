package com.voidframework.web.routing;

public interface RouteBuilder {

    RouteBuilder method(final HttpMethod httpMethod);

    RouteBuilder route(final String route);

    RouteBuilder call(final Class<?> controllerClass, final String methodName);
}
