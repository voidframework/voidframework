package dev.voidframework.web.exception;

import dev.voidframework.web.server.ExtraWebServerConfiguration;

/**
 * All exceptions related to the extra web server configuration are subclasses of {@code ExtraWebServerConfigurationException}.
 *
 * @since 1.3.0
 */
public class ExtraWebServerConfigurationException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     * @since 1.3.0
     */
    protected ExtraWebServerConfigurationException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.3.0
     */
    protected ExtraWebServerConfigurationException(final String message) {

        this(message, null);
    }

    /**
     * Exception indicates that request class was not found.
     *
     * @since 1.3.0
     */
    public static class ClassNotFound extends ExtraWebServerConfigurationException {

        /**
         * Build a new instance.
         *
         * @param className The class name
         * @since 1.3.0
         */
        public ClassNotFound(final String className) {

            super("Unable to resolve '" + ExtraWebServerConfiguration.class.getSimpleName() + "' implementation '" + className + "'");
        }
    }

    /**
     * Exception indicates that provided error handler is invalid.
     *
     * @since 1.3.0
     */
    public static class InvalidClass extends ExtraWebServerConfigurationException {

        /**
         * Build a new instance.
         *
         * @param className The class name
         * @since 1.3.0
         */
        public InvalidClass(final String className) {

            super("'" + className + "' must inherit from '" + ExtraWebServerConfiguration.class.getSimpleName() + "' interface");
        }
    }

    /**
     * Exception indicates that provided error handler can't be instantiated.
     *
     * @since 1.3.0
     */
    public static class CantInstantiate extends ExtraWebServerConfigurationException {

        /**
         * Build a new instance.
         *
         * @param className The class name
         * @since 1.3.0
         */
        public CantInstantiate(final String className) {

            super("Can't instantiate '" + className + "'");
        }

        /**
         * Build a new instance.
         *
         * @param className The class name
         * @param cause     The cause
         * @since 1.3.0
         */
        public CantInstantiate(final String className, final Exception cause) {

            super("Can't instantiate '" + className + "'", cause);
        }
    }
}
