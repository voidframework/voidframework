package dev.voidframework.web.exception;

/**
 * All exceptions related to the https web server configuration are subclasses of {@code HttpsWebServerConfigurationException}.
 *
 * @since 1.6.0
 */
public class HttpsWebServerConfigurationException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     * @since 1.6.0
     */
    protected HttpsWebServerConfigurationException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.6.0
     */
    protected HttpsWebServerConfigurationException(final String message) {

        this(message, null);
    }

    /**
     * Exception indicates that key store cannot be loaded.
     *
     * @since 1.6.0
     */
    public static class CannotLoadKeyStore extends HttpsWebServerConfigurationException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         * @since 1.6.0
         */
        public CannotLoadKeyStore(final Throwable cause) {

            super("Cannot load key store", cause);
        }
    }

    /**
     * Exception indicates that key manager cannot be initialized.
     *
     * @since 1.6.0
     */
    public static class KeyManagerInitFailure extends HttpsWebServerConfigurationException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         * @since 1.6.0
         */
        public KeyManagerInitFailure(final Throwable cause) {

            super("Key manager cannot be initialized", cause);
        }
    }

    /**
     * Exception indicates that SSL context cannot be initialized.
     *
     * @since 1.6.0
     */
    public static class SSLContextInitFailure extends HttpsWebServerConfigurationException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         * @since 1.6.0
         */
        public SSLContextInitFailure(final Throwable cause) {

            super("SSL context cannot be initialized", cause);
        }
    }

    /**
     * Exception indicates that requested key was not found.
     *
     * @since 1.6.0
     */
    public static class KeyNotFound extends HttpsWebServerConfigurationException {

        /**
         * Build a new instance.
         *
         * @param keyAlias The key alias
         * @since 1.6.0
         */
        public KeyNotFound(final String keyAlias) {

            super("Key identified by alias '" + keyAlias + "' was not found");
        }
    }
}
