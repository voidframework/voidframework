package dev.voidframework.healthcheck;

/**
 * This interface allows you to define a health checker.
 */
public interface HealthChecker {

    /**
     * Name of the health check.
     *
     * @return The health check name
     */
    String getName();

    /**
     * Checks the health of the component.
     *
     * @return The component's health status
     */
    Health checkHealth();
}
