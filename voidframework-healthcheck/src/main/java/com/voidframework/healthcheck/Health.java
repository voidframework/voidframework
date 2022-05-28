package com.voidframework.healthcheck;

import java.util.Map;

/**
 * Represents the health status of a component.
 *
 * @param status  The status
 * @param details Any details provided by the health checker
 */
public record Health(Status status, Map<String, Object> details) {

    /**
     * Possible status.
     */
    public enum Status {
        DOWN, UP,
    }
}
