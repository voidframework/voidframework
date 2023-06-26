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
     * Exception indicates that service identifier provided via
     * {@link dev.voidframework.restclient.annotation.RestClient} annotation is invalid.
     *
     * @since 1.9.0
     */
    public static class InvalidServiceIdentifier extends RestClientException {

        /**
         * Build a new instance.
         *
         * @since 1.9.0
         */
        public InvalidServiceIdentifier(final String serviceId) {

            super("'" + serviceId + "' is an invalid service identifier");
        }
    }

    /**
     * Exception indicates that something goes wrong during the call adapter process.
     *
     * @since 1.9.0
     */
    public static class CallAdapterProcessingException extends RestClientException {

        /**
         * Build a new instance.
         *
         * @since 1.9.0
         */
        public CallAdapterProcessingException(final Exception cause) {

            super("Can't complete call adapter process", cause);
        }
    }
}
