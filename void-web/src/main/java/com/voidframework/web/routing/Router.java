package com.voidframework.web.routing;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Router {

    void addRoute(final Function<RouteBuilder, RouteBuilder> builder);

    ResolvedRoute resolveRoute(final HttpMethod httpMethod, final String uri);

    List<Route> getRoutesAsList();

    Map<HttpMethod, List<Route>> getRoutesAsMap();
}
