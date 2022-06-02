package dev.voidframework.web.routing;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * A single route.
 *
 * @param httpMethod          HTTP method
 * @param routePattern        URL regex pattern
 * @param controllerClassType The controller class type
 * @param method              The method
 */
public record Route(HttpMethod httpMethod,
                    Pattern routePattern,
                    Class<?> controllerClassType,
                    Method method) {
}
