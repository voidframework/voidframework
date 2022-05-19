package com.voidframework.web.exception;

/**
 * All exceptions related to http possible errors are subclasses of {@code RoutingException}.
 */
public class HttpException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     */
    protected HttpException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected HttpException(final String message) {
        this(message, null);
    }

    /**
     * Exception indicates that a resource was not found.
     */
    public static class NotFound extends HttpException {

        /**
         * Build a new instance.
         */
        public NotFound() {
            this(null);
        }

        /**
         * Build a new instance.
         *
         * @param message The provided message
         */
        public NotFound(final String message) {
            super(message);
        }
    }
}
