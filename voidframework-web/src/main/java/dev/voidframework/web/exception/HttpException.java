package dev.voidframework.web.exception;

/**
 * All exceptions related to http possible errors are subclasses of {@code HttpException}.
 *
 * @since 1.0.0
 */
public class HttpException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     * @since 1.0.0
     */
    protected HttpException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.0.0
     */
    protected HttpException(final String message) {

        this(message, null);
    }

    /**
     * Exception indicates that provided request is invalid.
     *
     * @since 1.0.0
     */
    public static class BadRequest extends HttpException {

        /**
         * Build a new instance.
         *
         * @since 1.0.0
         */
        public BadRequest() {

            this(null);
        }

        /**
         * Build a new instance.
         *
         * @param message The provided message
         * @since 1.0.0
         */
        public BadRequest(final String message) {

            super(message);
        }

        /**
         * Build a new instance.
         *
         * @param message The provided message
         * @param cause   The cause
         * @since 1.0.0
         */
        public BadRequest(final String message, final Throwable cause) {

            super(message, cause);
        }
    }

    /**
     * Exception indicates that a server-side error occur.
     *
     * @since 1.0.0
     */
    public static class InternalServerError extends HttpException {

        /**
         * Build a new instance.
         *
         * @since 1.0.0
         */
        public InternalServerError() {

            this(null, null);
        }

        /**
         * Build a new instance.
         *
         * @param message The provided message
         * @since 1.0.0
         */
        public InternalServerError(final String message) {

            this(message, null);
        }

        /**
         * Build a new instance.
         *
         * @param cause The cause
         * @since 1.0.0
         */
        public InternalServerError(final Throwable cause) {

            this(cause.getMessage(), cause);
        }

        /**
         * Build a new instance.
         *
         * @param message The provided message
         * @param cause   The cause
         * @since 1.0.0
         */
        public InternalServerError(final String message, final Throwable cause) {

            super(message, cause);
        }
    }

    /**
     * Exception indicates that a resource was not found.
     *
     * @since 1.0.0
     */
    public static class NotFound extends HttpException {

        /**
         * Build a new instance.
         *
         * @since 1.0.0
         */
        public NotFound() {

            this(null);
        }

        /**
         * Build a new instance.
         *
         * @param message The provided message
         * @since 1.0.0
         */
        public NotFound(final String message) {

            super(message);
        }
    }
}
