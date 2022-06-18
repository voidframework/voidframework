package dev.voidframework.core.exception;

/**
 * All exceptions thrown by the app launcher are subclasses of {@code AppLauncherException}.
 */
public class AppLauncherException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected AppLauncherException(final String message) {
        this(message, null);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     */
    protected AppLauncherException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception indicates that application is already running.
     */
    public static class AlreadyRunning extends AppLauncherException {

        /**
         * Build a new instance.
         */
        public AlreadyRunning() {
            super("Application is already running");
        }
    }

    /**
     * Exception indicates that module initialization just fail.
     */
    public static class ModuleInitFailure extends AppLauncherException {

        /**
         * Build a new instance.
         *
         * @param moduleClass The module class
         * @param cause       The cause
         */
        public ModuleInitFailure(final Class<?> moduleClass, final Throwable cause) {
            super("Can't initialize Module '" + moduleClass + "'", cause);
        }
    }
}
