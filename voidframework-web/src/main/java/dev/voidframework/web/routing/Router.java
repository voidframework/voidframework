package dev.voidframework.web.routing;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Router.
 */
public interface Router {

    /**
     * Adds a new route.
     *
     * @param httpMethod          The HTTP method (ie: GET)
     * @param routeUrl            The route url
     * @param controllerClassType The controller class type
     * @param method              The method to call
     */
    void addRoute(final HttpMethod httpMethod, final String routeUrl, final Class<?> controllerClassType, final Method method);

    /**
     * Resolves a route.
     *
     * @param httpMethod The HTTP method (ie: GET)
     * @param uri        The URI to parse to resolve route
     * @return The resolved route, otherwise, {@code null}
     */
    ResolvedRoute resolveRoute(final HttpMethod httpMethod, final String uri);

    /**
     * Retrieves all registered routes.
     *
     * @return All registered routes as List
     */
    List<Route> getRoutesAsList();

    /**
     * Retrieves all registered routes.
     *
     * @return All registered routes as Map
     */
    Map<HttpMethod, List<Route>> getRoutesAsMap();
}
