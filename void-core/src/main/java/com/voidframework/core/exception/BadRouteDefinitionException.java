package com.voidframework.core.exception;

/**
 * All exceptions thrown by the router are subclasses of {@code BadRouteDefinitionException}.
 */
public class BadRouteDefinitionException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     */
    protected BadRouteDefinitionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected BadRouteDefinitionException(final String message) {
        this(message, null);
    }

    /**
     * Exception indicates that a value is invalid.
     */
    public static class BadValue extends BadRouteDefinitionException {

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
    public static class Missing extends BadRouteDefinitionException {

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
    public static class ControllerMethodDoesNotExists extends BadRouteDefinitionException {

        /**
         * Build a new instance.
         *
         * @param controllerClass The controller class
         * @param methodName      The method name
         */
        public ControllerMethodDoesNotExists(final Class<?> controllerClass, final String methodName) {
            super("Method '" + controllerClass.getName() + "::" + methodName + "' does not exists");
        }
    }

    /**
     * Exception indicates that given controller method don't return any kind of value.
     */
    public static class ControllerMethodDoesNotReturnsValue extends BadRouteDefinitionException {

        /**
         * Build a new instance.
         *
         * @param controllerClass The controller class
         * @param methodName      The method name
         */
        public ControllerMethodDoesNotReturnsValue(final Class<?> controllerClass, final String methodName) {
            super("Method '" + controllerClass.getName() + "::" + methodName + "' does not returns value");
        }
    }
}
