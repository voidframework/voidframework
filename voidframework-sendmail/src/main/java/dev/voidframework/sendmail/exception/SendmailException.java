package dev.voidframework.sendmail.exception;

/**
 * All exceptions thrown by the sendmail feature are subclasses of {@code SendmailException}.
 *
 * @since 1.7.0
 */
public class SendmailException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.7.0
     */
    protected SendmailException(final String message) {

        this(message, null);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     * @since 1.7.0
     */
    protected SendmailException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Exception indicates that mail is invalid because all mandatory
     * field are not filled in or have incorrect value.
     *
     * @since 1.7.0
     */
    public static class InvalidMail extends SendmailException {

        /**
         * Build a new instance.
         *
         * @since 1.7.0
         */
        public InvalidMail() {

            super("The email is invalid, please check that the required fields are filled in");
        }
    }
}
