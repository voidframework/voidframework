package dev.voidframework.core.exception;

/**
 * All exceptions thrown by the Duration utility class are subclasses of {@code DurationException}.
 */
public class DurationException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected DurationException(final String message) {

        this(message, null);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     */
    protected DurationException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Exception indicates that given value not a correct numeric value.
     */
    public static class InvalidNumericValue extends DurationException {

        /**
         * Build a new instance.
         *
         * @param givenValue The given numeric value
         * @param cause      The cause
         */
        public InvalidNumericValue(final String givenValue, final Throwable cause) {

            super("'" + givenValue + "' is not a valid numeric value", cause);
        }
    }

    /**
     * Exception indicates that given temporal unit is unknown.
     */
    public static class InvalidTemporalUnit extends DurationException {

        /**
         * Build a new instance.
         *
         * @param givenTemporalUnit The given temporal unit
         */
        public InvalidTemporalUnit(final String givenTemporalUnit) {

            super("'" + givenTemporalUnit + "' is not a valid temporal unit");
        }
    }

    /**
     * Exception indicates that numeric value part of the duration is missing.
     */
    public static class MissingNumericValue extends DurationException {

        /**
         * Build a new instance.
         */
        public MissingNumericValue() {

            super("Duration must start with a numeric value");
        }
    }

    /**
     * Exception indicates that temporal unit of the duration is missing.
     */
    public static class MissingTemporalUnit extends DurationException {

        /**
         * Build a new instance.
         */
        public MissingTemporalUnit() {

            super("Duration must end with a temporal unit");
        }
    }
}
