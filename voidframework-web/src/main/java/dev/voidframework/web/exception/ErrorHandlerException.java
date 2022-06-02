package dev.voidframework.web.exception;

/**
 * All exceptions related to the error handler are subclasses of {@code RoutingException}.
 */
public class ErrorHandlerException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     */
    protected ErrorHandlerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected ErrorHandlerException(final String message) {
        this(message, null);
    }

    /**
     * Exception indicates that request class was not found.
     */
    public static class ClassNotFound extends ErrorHandlerException {

        /**
         * Build a new instance.
         *
         * @param className The class name
         */
        public ClassNotFound(final String className) {
            super("Unable to resolve error handler '" + className + "'");
        }
    }

    /**
     * Exception indicates that provided error handler is invalid.
     */
    public static class InvalidClass extends ErrorHandlerException {

        /**
         * Build a new instance.
         *
         * @param className The class name
         */
        public InvalidClass(final String className) {
            super("'" + className + "' must inherit from ErrorHandler interface");
        }
    }

    /**
     * Exception indicates that provided error handler can't be instantiated.
     */
    public static class CantInstantiate extends ErrorHandlerException {

        /**
         * Build a new instance.
         *
         * @param className The class name
         */
        public CantInstantiate(final String className) {
            super("Can't instantiate '" + className + "'");
        }

        /**
         * Build a new instance.
         *
         * @param className The class name
         * @param cause     The cause exception
         */
        public CantInstantiate(final String className, final Exception cause) {
            super("Can't instantiate '" + className + "'", cause);
        }
    }

}
