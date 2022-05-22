package com.voidframework.web.routing;

import java.lang.reflect.Method;
import java.util.Map;

public record ResolvedRoute(Object controllerInstance,
                            Method method,
                            Map<String, String> extractedParameterValues) {
}
