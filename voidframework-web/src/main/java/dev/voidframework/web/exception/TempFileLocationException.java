package dev.voidframework.web.exception;

/**
 * All exceptions related to the temporary files location are subclasses of {@code TempFileLocationException}.
 *
 * @since 1.3.0
 */
public class TempFileLocationException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.3.0
     */
    protected TempFileLocationException(final String message) {

        super(message);
    }

    /**
     * Exception indicates that temporary files directory does not exist and can't be created.
     *
     * @since 1.3.0
     */
    public static class DirectoryCreationFailure extends RuntimeException {

        /**
         * Build a new instance.
         *
         * @param tempFileLocation The temporary files directory location (ie: /tmp/voidframework)
         * @since 1.3.0
         */
        public DirectoryCreationFailure(final String tempFileLocation) {

            super("Can't create temporary file directory '" + tempFileLocation + "'");
        }
    }
}
