package dev.voidframework.healthcheck.checker;

import dev.voidframework.healthcheck.Health;
import dev.voidframework.healthcheck.HealthChecker;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Java Virtual Machine health checker.
 */
public class JavaVirtualMachineHealthChecker implements HealthChecker {

    @Override
    public String getName() {
        return "JVM";
    }

    @Override
    public Health checkHealth() {
        final Map<String, Object> detailsMap = new LinkedHashMap<>();

        detailsMap.put("javaVersion", Runtime.version().toString());
        detailsMap.put("maxMemory", Runtime.getRuntime().maxMemory());
        detailsMap.put("totalMemory", Runtime.getRuntime().totalMemory());
        detailsMap.put("freeMemory", Runtime.getRuntime().freeMemory());

        return new Health(Health.Status.UP, detailsMap);
    }
}
