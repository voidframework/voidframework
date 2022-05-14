package com.voidframework.core.routing;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface Router {

    void addRoute(final Function<RouteBuilder, RouteBuilder> builder);

    Optional<ResolvedRoute> resolveRoute(final HttpMethod httpMethod, final String uri);

    List<Route> getRoutesAsList();

    Map<HttpMethod, List<Route>> getRoutesAsMap();
}
