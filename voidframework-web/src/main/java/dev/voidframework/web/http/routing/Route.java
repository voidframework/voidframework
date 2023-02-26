package dev.voidframework.web.http.routing;

import dev.voidframework.web.http.HttpMethod;
import dev.voidframework.web.http.filter.Filter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A single route.
 *
 * @param httpMethod          HTTP method
 * @param routePattern        URL regex pattern
 * @param filterClassTypes    The filter class types to apply
 * @param controllerClassType The controller class type
 * @param method              The method
 * @since 1.0.0
 */
public record Route(HttpMethod httpMethod,
                    Pattern routePattern,
                    List<Class<? extends Filter>> filterClassTypes,
                    Class<?> controllerClassType,
                    Method method) {
}
