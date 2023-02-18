package dev.voidframework.web.http.routing;

import dev.voidframework.web.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Router.
 *
 * @since 1.0.0
 */
public interface Router {

    /**
     * Adds a new route.
     *
     * @param httpMethod          The HTTP method (ie: GET)
     * @param routeURL            The route URL
     * @param controllerClassType The controller class type
     * @param method              The method to
     * @since 1.0.0
     */
    void addRoute(final HttpMethod httpMethod,
                  final RouteURL routeURL,
                  final Class<?> controllerClassType,
                  final Method method);

    /**
     * Adds a new route.
     *
     * @param httpMethod          The HTTP method (ie: GET)
     * @param routeURL            The route URL
     * @param controllerClassType The controller class type
     * @param method              The method to call
     * @param name                The route name
     * @since 1.0.0
     */
    void addRoute(final HttpMethod httpMethod,
                  final RouteURL routeURL,
                  final Class<?> controllerClassType,
                  final Method method,
                  final String name);

    /**
     * Resolves a route.
     *
     * @param httpMethod The HTTP method (ie: GET)
     * @param uri        The URI to parse to resolve route
     * @return The resolved route, otherwise, {@code null}
     * @since 1.0.0
     */
    ResolvedRoute resolveRoute(final HttpMethod httpMethod,
                               final String uri);

    /**
     * Reverses a route to obtains a URL.
     *
     * @param name The route name
     * @return A string containing a URL
     * @since 1.0.0
     */
    String reverseRoute(final String name);

    /**
     * Reverses a route to obtains a URL.
     *
     * @param name          The route name
     * @param parameterList The parameters to use
     * @return A string containing a URL
     * @since 1.0.0
     */
    String reverseRoute(final String name, final List<Object> parameterList);

    /**
     * Retrieves all registered routes.
     *
     * @return All registered routes as List
     * @since 1.0.0
     */
    List<Route> getRoutesAsList();

    /**
     * Retrieves all registered routes.
     *
     * @return All registered routes as Map
     * @since 1.0.0
     */
    Map<HttpMethod, List<Route>> getRoutesAsMap();
}
