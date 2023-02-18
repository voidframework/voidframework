package dev.voidframework.healthcheck;

import java.util.Map;

/**
 * Represents the health status of a component.
 *
 * @param status  The status
 * @param details Any details provided by the health checker
 * @since 1.0.0
 */
public record Health(Status status, Map<String, Object> details) {

    /**
     * Possible status.
     *
     * @since 1.0.0
     */
    public enum Status {

        /**
         * Service is not running... or dead.
         *
         * @since 1.0.0
         */
        DOWN,

        /**
         * Service is running.
         *
         * @since 1.0.0
         */
        UP,
    }
}
