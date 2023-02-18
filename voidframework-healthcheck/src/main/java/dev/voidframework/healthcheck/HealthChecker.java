package dev.voidframework.healthcheck;

/**
 * This interface allows you to define a health checker.
 *
 * @since 1.0.0
 */
public interface HealthChecker {

    /**
     * Name of the health check.
     *
     * @return The health check name
     * @since 1.0.0
     */
    String getName();

    /**
     * Checks the health of the component.
     *
     * @return The component's health status
     * @since 1.0.0
     */
    Health checkHealth();
}
