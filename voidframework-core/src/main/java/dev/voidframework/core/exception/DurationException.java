package dev.voidframework.core.exception;

/**
 * All exceptions thrown by the Duration utility class are subclasses of {@code DurationException}.
 *
 * @since 1.6.0
 */
public class DurationException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.6.0
     */
    protected DurationException(final String message) {

        this(message, null);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     * @since 1.6.0
     */
    protected DurationException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Exception indicates that given value not a correct numeric value.
     *
     * @since 1.6.0
     */
    public static class InvalidNumericValue extends DurationException {

        /**
         * Build a new instance.
         *
         * @param givenValue The given numeric value
         * @param cause      The cause
         * @since 1.6.0
         */
        public InvalidNumericValue(final String givenValue, final Throwable cause) {

            super("'" + givenValue + "' is not a valid numeric value", cause);
        }
    }

    /**
     * Exception indicates that given temporal unit is unknown.
     *
     * @since 1.6.0
     */
    public static class InvalidTemporalUnit extends DurationException {

        /**
         * Build a new instance.
         *
         * @param givenTemporalUnit The given temporal unit
         * @since 1.6.0
         */
        public InvalidTemporalUnit(final String givenTemporalUnit) {

            super("'" + givenTemporalUnit + "' is not a valid temporal unit");
        }
    }

    /**
     * Exception indicates that numeric value part of the duration is missing.
     *
     * @since 1.6.0
     */
    public static class MissingNumericValue extends DurationException {

        /**
         * Build a new instance.
         *
         * @since 1.6.0
         */
        public MissingNumericValue() {

            super("Duration must start with a numeric value");
        }
    }

    /**
     * Exception indicates that temporal unit of the duration is missing.
     *
     * @since 1.6.0
     */
    public static class MissingTemporalUnit extends DurationException {

        /**
         * Build a new instance.
         *
         * @since 1.6.0
         */
        public MissingTemporalUnit() {

            super("Duration must end with a temporal unit");
        }
    }
}
