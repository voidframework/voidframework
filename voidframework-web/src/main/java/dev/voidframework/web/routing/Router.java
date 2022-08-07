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
    void addRoute(final HttpMethod httpMethod,
                  final String routeUrl,
                  final Class<?> controllerClassType,
                  final Method method);

    /**
     * Adds a new route.
     *
     * @param httpMethod          The HTTP method (ie: GET)
     * @param routeUrl            The route url
     * @param controllerClassType The controller class type
     * @param method              The method to call
     * @param name                The route name
     */
    void addRoute(final HttpMethod httpMethod,
                  final String routeUrl,
                  final Class<?> controllerClassType,
                  final Method method,
                  final String name);

    /**
     * Resolves a route.
     *
     * @param httpMethod The HTTP method (ie: GET)
     * @param uri        The URI to parse to resolve route
     * @return The resolved route, otherwise, {@code null}
     */
    ResolvedRoute resolveRoute(final HttpMethod httpMethod,
                               final String uri);

    /**
     * Reverses a route to obtains a URL.
     *
     * @param name The route name
     * @return A string containing a URL
     */
    String reverseRoute(final String name);

    /**
     * Reverses a route to obtains a URL.
     *
     * @param name          The route name
     * @param parameterList The parameters to use
     * @return A string containing a URL
     */
    String reverseRoute(final String name, final List<Object> parameterList);

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
