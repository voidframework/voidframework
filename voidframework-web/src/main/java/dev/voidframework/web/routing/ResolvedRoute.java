package dev.voidframework.web.routing;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Represents a resolved route.
 *
 * @param controllerClassType      The controller class type
 * @param method                   The method to call
 * @param extractedParameterValues The extracted parameters from the route
 */
public record ResolvedRoute(Class<?> controllerClassType,
                            Method method,
                            Map<String, String> extractedParameterValues) {
}
