package dev.voidframework.restclient.exception;

/**
 * All exceptions related to REST Client feature are subclasses of {@code RestClientException}.
 *
 * @since 1.9.0
 */
public class RestClientException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     * @since 1.9.0
     */
    protected RestClientException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.9.0
     */
    protected RestClientException(final String message) {

        this(message, null);
    }

    /**
     * Exception indicates that provided request is invalid.
     *
     * @since 1.9.0
     */
    public static class IncompatibleReturnType extends RestClientException {

        /**
         * Build a new instance.
         *
         * @since 1.9.0
         */
        public IncompatibleReturnType(final Class<?> returnTypeClass) {

            super("Return type '" + returnTypeClass.getSimpleName() + "' is not compatible");
        }
    }

    /**
     * Exception indicates that provided request is invalid.
     *
     * @since 1.9.0
     */
    public static class RequiredAnnotationMissing extends RestClientException {

        /**
         * Build a new instance.
         *
         * @since 1.9.0
         */
        public RequiredAnnotationMissing() {

            this("At least one required annotation is missing");
        }

        /**
         * Build a new instance.
         *
         * @param message The provided message
         * @since 1.9.0
         */
        public RequiredAnnotationMissing(final String message) {

            super(message);
        }
    }
}
