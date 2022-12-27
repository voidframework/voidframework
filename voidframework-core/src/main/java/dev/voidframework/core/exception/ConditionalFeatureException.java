package dev.voidframework.core.exception;

import dev.voidframework.core.conditionalfeature.condition.Condition;

/**
 * All exceptions thrown by conditional feature are subclasses of {@code ConditionalFeatureException}.
 */
public class ConditionalFeatureException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     */
    protected ConditionalFeatureException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * This exception indicate that condition cannot be instantiated.
     */
    public static class ConditionInitFailure extends RemoteConfigurationException {

        /**
         * Build a new instance.
         *
         * @param condition The condition class type
         * @param cause     The cause
         */
        public ConditionInitFailure(final Class<? extends Condition> condition, final Throwable cause) {

            super("Cannot initialize condition '" + condition.getName() + "'", cause);
        }
    }
}
