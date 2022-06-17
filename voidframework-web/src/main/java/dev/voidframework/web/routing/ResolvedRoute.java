package dev.voidframework.web.routing;

import dev.voidframework.web.filter.Filter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Represents a resolved route.
 *
 * @param filterClassTypes         The filter class types to apply
 * @param controllerClassType      The controller class type
 * @param method                   The method to call
 * @param extractedParameterValues The extracted parameters from the route
 */
public record ResolvedRoute(List<Class<? extends Filter>> filterClassTypes,
                            Class<?> controllerClassType,
                            Method method,
                            Map<String, String> extractedParameterValues) {
}
