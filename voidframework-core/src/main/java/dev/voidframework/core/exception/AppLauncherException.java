package dev.voidframework.core.exception;

/**
 * All exceptions thrown by the app launcher are subclasses of {@code AppLauncherException}.
 *
 * @since 1.0.0
 */
public class AppLauncherException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.0.0
     */
    protected AppLauncherException(final String message) {

        this(message, null);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     * @since 1.0.0
     */
    protected AppLauncherException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Exception indicates that application is already running.
     *
     * @since 1.0.0
     */
    public static class AlreadyRunning extends AppLauncherException {

        /**
         * Build a new instance.
         *
         * @since 1.0.0
         */
        public AlreadyRunning() {

            super("Application is already running");
        }
    }

    /**
     * Exception indicates that module initialization just fail.
     *
     * @since 1.0.0
     */
    public static class ModuleInitFailure extends AppLauncherException {

        /**
         * Build a new instance.
         *
         * @param moduleClass The module class
         * @param cause       The cause
         * @since 1.0.0
         */
        public ModuleInitFailure(final Class<?> moduleClass, final Throwable cause) {

            super("Can't initialize Module '" + moduleClass + "'", cause);
        }
    }
}
