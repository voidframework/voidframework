package com.voidframework.core.routing;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * A single route.
 *
 * @param httpMethod      HTTP method
 * @param routePattern    URL regex pattern
 * @param controllerClass The controller class
 * @param method          The method
 */
public record Route(HttpMethod httpMethod,
                    Pattern routePattern,
                    Class<?> controllerClass,
                    Method method) {
}
