package dev.voidframework.core.exception;

import dev.voidframework.core.remoteconfiguration.RemoteConfigurationProvider;

/**
 * All exceptions thrown by the remote configuration are subclasses of {@code RemoteConfigurationException}.
 */
public class RemoteConfigurationException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param providerClassName The provider class name
     * @param message           The detail message
     */
    protected RemoteConfigurationException(final String providerClassName, final String message) {

        this("Provider '" + providerClassName + "' " + message, (Throwable) null);
    }

    /**
     * Build a new instance.
     *
     * @param providerClassName The provider class name
     * @param message           The detail message
     * @param cause             The cause
     */
    protected RemoteConfigurationException(final String providerClassName, final String message, final Throwable cause) {

        this("Provider '" + providerClassName + "' " + message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected RemoteConfigurationException(final String message) {

        this(message, (Throwable) null);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     */
    protected RemoteConfigurationException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * This exception indicate that provider does not exist.
     */
    public static class ProviderDoesNotExist extends RemoteConfigurationException {

        /**
         * Build a new instance.
         *
         * @param providerClassPath The provider class path
         */
        public ProviderDoesNotExist(final String providerClassPath) {

            super(providerClassPath, "does not exist");
        }
    }

    /**
     * This exception indicate that provider fail to fetch remote configuration.
     */
    public static class FetchError extends RemoteConfigurationException {

        /**
         * Build a new instance.
         *
         * @param providerClass The provider class
         * @param cause         The cause
         */
        public FetchError(final Class<? extends RemoteConfigurationProvider> providerClass, final String cause) {

            super(providerClass.getName(), "can't fetch remote configuration: " + cause);
        }

        /**
         * Build a new instance.
         *
         * @param providerClass The provider class
         * @param cause         The cause
         */
        public FetchError(final Class<? extends RemoteConfigurationProvider> providerClass, final Throwable cause) {

            super(providerClass.getName(), "can't fetch remote configuration", cause);
        }
    }

    /**
     * This exception indicate that provider can't be instantiated / used.
     */
    public static class BadProvider extends RemoteConfigurationException {

        /**
         * Build a new instance.
         *
         * @param providerClassPath The provider class path
         * @param cause             The cause
         */
        public BadProvider(final String providerClassPath, final Throwable cause) {

            super(providerClassPath, "is invalid", cause);
        }
    }

    /**
     * This exception indicate that a value was not valid.
     */
    public static class BadValue extends RemoteConfigurationException {

        /**
         * Build a new instance.
         *
         * @param path    The value path
         * @param message The error message
         * @param cause   The cause
         */
        public BadValue(String path, String message, Throwable cause) {

            super("Invalid value at '" + path + "': " + message, cause);
        }

        /**
         * Build a new instance.
         *
         * @param path    The value path
         * @param message The error message
         */
        public BadValue(String path, String message) {

            this(path, message, null);
        }
    }

    /**
     * This exception indicate that file can't be saved.
     */
    public static class StorageException extends RemoteConfigurationException {

        /**
         * Build a new instance.
         *
         * @param path    The key path
         * @param message The detail message
         */
        public StorageException(final String path, final String message) {

            this(path, message, null);
        }

        /**
         * Build a new instance.
         *
         * @param path    The key path
         * @param message The detail message
         * @param cause   The cause
         */
        public StorageException(final String path, final String message, final Exception cause) {

            super("Can't save the file '" + path + "': " + message, cause);
        }
    }
}
