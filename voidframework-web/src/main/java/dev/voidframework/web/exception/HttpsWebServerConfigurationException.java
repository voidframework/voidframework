package dev.voidframework.web.exception;

/**
 * All exceptions related to the https web server configuration are subclasses of {@code HttpsWebServerConfigurationException}.
 */
public class HttpsWebServerConfigurationException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     */
    protected HttpsWebServerConfigurationException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected HttpsWebServerConfigurationException(final String message) {

        this(message, null);
    }

    /**
     * Exception indicates that key store cannot be loaded.
     */
    public static class CannotLoadKeyStore extends HttpsWebServerConfigurationException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public CannotLoadKeyStore(final Throwable cause) {

            super("Cannot load key store", cause);
        }
    }

    /**
     * Exception indicates that key manager cannot be initialized.
     */
    public static class KeyManagerInitFailure extends HttpsWebServerConfigurationException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public KeyManagerInitFailure(final Throwable cause) {

            super("Key manager cannot be initialized", cause);
        }
    }

    /**
     * Exception indicates that SSL context cannot be initialized.
     */
    public static class SSLContextInitFailure extends HttpsWebServerConfigurationException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public SSLContextInitFailure(final Throwable cause) {

            super("SSL context cannot be initialized", cause);
        }
    }

    /**
     * Exception indicates that requested key was not found.
     */
    public static class KeyNotFound extends HttpsWebServerConfigurationException {

        /**
         * Build a new instance.
         *
         * @param keyAlias The key alias
         */
        public KeyNotFound(final String keyAlias) {

            super("Key identified by alias '" + keyAlias + "' was not found");
        }
    }
}
