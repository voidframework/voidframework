package dev.voidframework.core.exception;

import dev.voidframework.core.remoteconfiguration.RemoteConfigurationProvider;

/**
 * All exceptions thrown by the remote configuration are subclasses of {@code RemoteConfigurationException}.
 *
 * @since 1.2.0
 */
public class RemoteConfigurationException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param providerClassName The provider class name
     * @param message           The detail message
     * @since 1.2.0
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
     * @since 1.2.0
     */
    protected RemoteConfigurationException(final String providerClassName, final String message, final Throwable cause) {

        this("Provider '" + providerClassName + "' " + message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.2.0
     */
    protected RemoteConfigurationException(final String message) {

        this(message, (Throwable) null);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     * @since 1.2.0
     */
    protected RemoteConfigurationException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * This exception indicate that provider does not exist.
     *
     * @since 1.2.0
     */
    public static class ProviderDoesNotExist extends RemoteConfigurationException {

        /**
         * Build a new instance.
         *
         * @param providerClassPath The provider class path
         * @since 1.2.0
         */
        public ProviderDoesNotExist(final String providerClassPath) {

            super(providerClassPath, "does not exist");
        }
    }

    /**
     * This exception indicate that provider fail to fetch remote configuration.
     *
     * @since 1.2.0
     */
    public static class FetchError extends RemoteConfigurationException {

        /**
         * Build a new instance.
         *
         * @param providerClass The provider class
         * @param cause         The cause
         * @since 1.2.0
         */
        public FetchError(final Class<? extends RemoteConfigurationProvider> providerClass, final String cause) {

            super(providerClass.getName(), "can't fetch remote configuration: " + cause);
        }

        /**
         * Build a new instance.
         *
         * @param providerClass The provider class
         * @param cause         The cause
         * @since 1.2.0
         */
        public FetchError(final Class<? extends RemoteConfigurationProvider> providerClass, final Throwable cause) {

            super(providerClass.getName(), "can't fetch remote configuration", cause);
        }
    }

    /**
     * This exception indicate that provider can't be instantiated / used.
     *
     * @since 1.2.0
     */
    public static class BadProvider extends RemoteConfigurationException {

        /**
         * Build a new instance.
         *
         * @param providerClassPath The provider class path
         * @param cause             The cause
         * @since 1.2.0
         */
        public BadProvider(final String providerClassPath, final Throwable cause) {

            super(providerClassPath, "is invalid", cause);
        }
    }

    /**
     * This exception indicate that a value was not valid.
     *
     * @since 1.2.0
     */
    public static class BadValue extends RemoteConfigurationException {

        /**
         * Build a new instance.
         *
         * @param path    The value path
         * @param message The error message
         * @param cause   The cause
         * @since 1.2.0
         */
        public BadValue(final String path,
                        final String message,
                        final Throwable cause) {

            super("Invalid value at '" + path + "': " + message, cause);
        }

        /**
         * Build a new instance.
         *
         * @param path    The value path
         * @param message The error message
         * @since 1.2.0
         */
        public BadValue(final String path, final String message) {

            this(path, message, null);
        }
    }

    /**
     * This exception indicate that file can't be saved.
     *
     * @since 1.2.0
     */
    public static class StorageException extends RemoteConfigurationException {

        /**
         * Build a new instance.
         *
         * @param path    The key path
         * @param message The detail message
         * @since 1.2.0
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
         * @since 1.2.0
         */
        public StorageException(final String path,
                                final String message,
                                final Exception cause) {

            super("Can't save the file '" + path + "': " + message, cause);
        }
    }
}
