package dev.voidframework.vfs.exception;

/**
 * All exceptions thrown by the VFS feature are subclasses of {@code VirtualFileStorageException}.
 */
public class VirtualFileStorageException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected VirtualFileStorageException(final String message) {

        this(message, null);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     */
    protected VirtualFileStorageException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Exception indicates that no VFS configuration have been found.
     */
    public static class EngineNotFound extends VirtualFileStorageException {

        /**
         * Build a new instance.
         *
         * @param engineClassPath The engine class path
         */
        public EngineNotFound(final String engineClassPath) {

            super("Virtual File Storage engine '" + engineClassPath + "' not found");
        }
    }

    /**
     * Exception indicates that no VFS configuration have been found.
     */
    public static class NotConfigured extends VirtualFileStorageException {

        /**
         * Build a new instance.
         */
        public NotConfigured() {

            super("Virtual File Storage is not configured");
        }
    }

    /**
     * Exception indicates that VFS engine can't be instantiated.
     */
    public static class CantInstantiateEngine extends VirtualFileStorageException {

        /**
         * Build a new instance.
         */
        public CantInstantiateEngine() {

            super("Virtual File Storage can't be instantiated. It must have a public empty constructor or a public constructor with Config parameter");
        }
    }

}
