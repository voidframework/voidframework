package dev.voidframework.core.exception;

/**
 * All exceptions thrown by the JSON utility class are subclasses of {@code JsonException}.
 */
public class JsonException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     */
    protected JsonException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Exception indicates that conversion to a JSON string just fail.
     */
    public static class ToStringConversionFailure extends JsonException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public ToStringConversionFailure(final Throwable cause) {

            super("To JSON string conversion failure", cause);
        }
    }

    /**
     * Exception indicates that conversion to JSON just fail.
     */
    public static class ToJsonConversionFailure extends JsonException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public ToJsonConversionFailure(final Throwable cause) {

            super("To JSON conversion failure", cause);
        }
    }

    /**
     * Exception indicates that conversion from JSON just fail.
     */
    public static class FromJsonConversionFailure extends JsonException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public FromJsonConversionFailure(final Throwable cause) {

            super("From JSON conversion failure", cause);
        }
    }
}
