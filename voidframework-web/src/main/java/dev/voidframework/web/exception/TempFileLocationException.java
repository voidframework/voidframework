package dev.voidframework.web.exception;

/**
 * All exceptions related to the temporary files location are subclasses of {@code TempFileLocationException}.
 */
public class TempFileLocationException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected TempFileLocationException(final String message) {

        super(message);
    }

    /**
     * Exception indicates that temporary files directory does not exist and can't be created.
     */
    public static class DirectoryCreationFailure extends RuntimeException {

        /**
         * Build a new instance.
         *
         * @param tempFileLocation The temporary files directory location (ie: /tmp/voidframework)
         */
        public DirectoryCreationFailure(final String tempFileLocation) {

            super("Can't create temporary file directory '" + tempFileLocation + "'");
        }
    }
}
