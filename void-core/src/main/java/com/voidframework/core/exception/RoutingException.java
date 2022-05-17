package com.voidframework.core.exception;

/**
 * All exceptions thrown by the routing features are subclasses of {@code RoutingException}.
 */
public class RoutingException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     */
    protected RoutingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected RoutingException(final String message) {
        this(message, null);
    }

    /**
     * Exception indicates that a value is invalid.
     */
    public static class BadValue extends RoutingException {

        /**
         * Build a new instance.
         *
         * @param valueName The name of the missing value
         * @param message   The error message
         * @param cause     The root cause
         */
        public BadValue(final String valueName, final String message, final Throwable cause) {
            super("Invalid value '" + valueName + "': " + message, cause);
        }

        /**
         * Build a new instance.
         *
         * @param valueName The name of the missing value
         * @param message   The error message
         */
        public BadValue(final String valueName, final String message) {
            super("Invalid value '" + valueName + "': " + message);
        }
    }

    /**
     * Exception indicates that a value was never set to anything, or set to null.
     */
    public static class Missing extends RoutingException {

        /**
         * Build a new instance.
         *
         * @param valueName The name of the missing value
         * @param cause     The root cause
         */
        public Missing(final String valueName, final Throwable cause) {
            super("Value '" + valueName + "' is missing", cause);
        }

        /**
         * Build a new instance.
         *
         * @param valueName The name of the missing value
         */
        public Missing(final String valueName) {
            super("Value '" + valueName + "' is missing");
        }
    }

    /**
     * Exception indicates that given controller method name does not match with an existing method.
     */
    public static class ControllerMethodDoesNotExists extends RoutingException {

        /**
         * Build a new instance.
         *
         * @param controllerClass The controller class
         * @param methodName      The method name
         * @param parameterCount  The method parameters count
         */
        public ControllerMethodDoesNotExists(final Class<?> controllerClass, final String methodName, final int parameterCount) {
            super("Method '" + controllerClass.getName() + "::" + methodName + "' with " + parameterCount + " parameter(s) does not exists");
        }
    }

    /**
     * Exception indicates that given controller method don't return a Result.
     */
    public static class ControllerMethodMustReturnResult extends RoutingException {

        /**
         * Build a new instance.
         *
         * @param controllerClass The controller class
         * @param methodName      The method name
         */
        public ControllerMethodMustReturnResult(final Class<?> controllerClass, final String methodName) {
            super("Method '" + controllerClass.getName() + "::" + methodName + "' must return a Result");
        }
    }
}
