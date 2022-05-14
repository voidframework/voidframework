package com.voidframework.core.routing;

import java.lang.reflect.Method;
import java.util.Map;

public record ResolvedRoute(Class<?> controllerClass,
                            Method method,
                            Map<String, String> extractedParameterValues) {
}
