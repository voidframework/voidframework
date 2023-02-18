package dev.voidframework.vfs.exception;

/**
 * All exceptions thrown by the VFS feature are subclasses of {@code VirtualFileStorageException}.
 *
 * @since 1.3.0
 */
public class VirtualFileStorageException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.3.0
     */
    protected VirtualFileStorageException(final String message) {

        this(message, null);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     * @since 1.3.0
     */
    protected VirtualFileStorageException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Exception indicates that no VFS configuration have been found.
     *
     * @since 1.3.0
     */
    public static class EngineNotFound extends VirtualFileStorageException {

        /**
         * Build a new instance.
         *
         * @param engineClassPath The engine class path
         * @since 1.3.0
         */
        public EngineNotFound(final String engineClassPath) {

            super("Virtual File Storage engine '" + engineClassPath + "' not found");
        }
    }

    /**
     * Exception indicates that no VFS configuration have been found.
     *
     * @since 1.3.0
     */
    public static class NotConfigured extends VirtualFileStorageException {

        /**
         * Build a new instance.
         *
         * @since 1.3.0
         */
        public NotConfigured() {

            super("Virtual File Storage is not configured");
        }
    }

    /**
     * Exception indicates that VFS engine can't be instantiated.
     *
     * @since 1.3.0
     */
    public static class CantInstantiateEngine extends VirtualFileStorageException {

        /**
         * Build a new instance.
         *
         * @since 1.3.0
         */
        public CantInstantiateEngine() {

            super("Virtual File Storage can't be instantiated. It must have a public empty constructor or a public constructor with Config parameter");
        }
    }

}
