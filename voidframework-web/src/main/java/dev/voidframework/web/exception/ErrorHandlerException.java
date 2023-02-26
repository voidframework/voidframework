package dev.voidframework.web.exception;

import dev.voidframework.web.http.errorhandler.ErrorHandler;

/**
 * All exceptions related to the error handler are subclasses of {@code ErrorHandlerException}.
 *
 * @since 1.0.0
 */
public class ErrorHandlerException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     * @since 1.0.0
     */
    protected ErrorHandlerException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.0.0
     */
    protected ErrorHandlerException(final String message) {

        this(message, null);
    }

    /**
     * Exception indicates that request class was not found.
     *
     * @since 1.0.0
     */
    public static class ClassNotFound extends ErrorHandlerException {

        /**
         * Build a new instance.
         *
         * @param className The class name
         * @since 1.0.0
         */
        public ClassNotFound(final String className) {

            super("Unable to resolve '" + ErrorHandler.class.getSimpleName() + "' implementation '" + className + "'");
        }
    }

    /**
     * Exception indicates that provided error handler is invalid.
     *
     * @since 1.0.0
     */
    public static class InvalidClass extends ErrorHandlerException {

        /**
         * Build a new instance.
         *
         * @param className The class name
         * @since 1.0.0
         */
        public InvalidClass(final String className) {

            super("'" + className + "' must inherit from '" + ErrorHandler.class.getSimpleName() + "' interface");
        }
    }

    /**
     * Exception indicates that provided error handler can't be instantiated.
     *
     * @since 1.0.0
     */
    public static class CantInstantiate extends ErrorHandlerException {

        /**
         * Build a new instance.
         *
         * @param className The class name
         * @since 1.0.0
         */
        public CantInstantiate(final String className) {

            super("Can't instantiate '" + className + "'");
        }

        /**
         * Build a new instance.
         *
         * @param className The class name
         * @param cause     The cause
         * @since 1.0.0
         */
        public CantInstantiate(final String className, final Exception cause) {

            super("Can't instantiate '" + className + "'", cause);
        }
    }

}
