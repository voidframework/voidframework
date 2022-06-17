package dev.voidframework.web.exception;

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
     * Exception indicates that provided request is invalid.
     */
    public static class BadRequest extends HttpException {

        /**
         * Build a new instance.
         */
        public BadRequest() {
            this(null);
        }

        /**
         * Build a new instance.
         *
         * @param message The provided message
         */
        public BadRequest(final String message) {
            super(message);
        }

        /**
         * Build a new instance.
         *
         * @param message The provided message
         * @param cause   The cause
         */
        public BadRequest(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception indicates that a server-side error occur.
     */
    public static class InternalServerError extends HttpException {

        /**
         * Build a new instance.
         */
        public InternalServerError() {
            this(null, null);
        }

        /**
         * Build a new instance.
         *
         * @param message The provided message
         */
        public InternalServerError(final String message) {
            this(message, null);
        }

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public InternalServerError(final Throwable cause) {
            this(cause.getMessage(), cause);
        }

        /**
         * Build a new instance.
         *
         * @param message The provided message
         * @param cause   The cause
         */
        public InternalServerError(final String message, final Throwable cause) {
            super(message, cause);
        }
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
