package com.voidframework.web.routing;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public interface Router {

    void addRoute(final HttpMethod httpMethod, final String routeUrl, final Class<?> controllerClassType, final Method method);

    ResolvedRoute resolveRoute(final HttpMethod httpMethod, final String uri);

    List<Route> getRoutesAsList();

    Map<HttpMethod, List<Route>> getRoutesAsMap();
}
