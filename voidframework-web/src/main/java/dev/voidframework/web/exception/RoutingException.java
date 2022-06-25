package dev.voidframework.web.exception;

/**
 * All exceptions related to the routing feature are subclasses of {@code RoutingException}.
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
     * Exception indicates that app defined route definition class can't be loaded.
     */
    public static class AppRouteDefinitionLoadFailure extends RoutingException {

        /**
         * Build a new instance.
         *
         * @param appRoutesDefinitionClassName The app defined route definition class name
         */
        public AppRouteDefinitionLoadFailure(final String appRoutesDefinitionClassName) {

            super("Can't find routes definition '" + appRoutesDefinitionClassName + "'");
        }
    }

    /**
     * Exception indicates that an argument during the route registration is invalid.
     */
    public static class BadRoutingArgument extends RoutingException {

        /**
         * Build a new instance.
         *
         * @param argumentName The argument name
         * @param currentValue The current invalid value
         */
        public BadRoutingArgument(final String argumentName, final Object currentValue) {

            super("The argument '" + argumentName + "' is invalid, current value is: " + currentValue);
        }
    }
}
