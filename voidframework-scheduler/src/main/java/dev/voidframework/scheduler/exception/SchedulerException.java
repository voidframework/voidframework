package dev.voidframework.scheduler.exception;

/**
 * All exceptions thrown by the scheduler feature are subclasses of {@code SchedulerException}.
 */
public class SchedulerException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected SchedulerException(final String message) {

        this(message, null);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     */
    protected SchedulerException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Exception indicates that delay and fixed rate values are filed, but are mutually exclusive.
     */
    public static class FixedDelayAndRateAreExclusive extends SchedulerException {

        /**
         * Build a new instance.
         */
        public FixedDelayAndRateAreExclusive() {

            super("Values fixedDelay and fixedRate are mutually exclusive");
        }
    }

    /**
     * Exception indicates that fixed delay value is invalid.
     */
    public static class InvalidFixedDelay extends SchedulerException {

        /**
         * Build a new instance.
         *
         * @param fixedRate The current fixed rate
         */
        public InvalidFixedDelay(final int fixedRate) {

            super("Value fixedDelay %d is invalid".formatted(fixedRate));
        }
    }

    /**
     * Exception indicates that fixed rate value is invalid.
     */
    public static class InvalidFixedRate extends SchedulerException {

        /**
         * Build a new instance.
         *
         * @param fixedRate The current fixed rate
         */
        public InvalidFixedRate(final int fixedRate) {

            super("Value fixedRate %d is invalid".formatted(fixedRate));
        }
    }

    /**
     * Exception indicates that initial delay value is invalid.
     */
    public static class InvalidInitialDelay extends SchedulerException {

        /**
         * Build a new instance.
         *
         * @param initialDelay The current initial delay
         */
        public InvalidInitialDelay(final int initialDelay) {

            super("The initial delay %d is invalid".formatted(initialDelay));
        }
    }

    /**
     * Exception indicates that CRON expression is invalid.
     */
    public static class InvalidCronExpression extends SchedulerException {

        /**
         * Build a new instance.
         *
         * @param errorMessage     The error message format
         * @param stringFormatArgs The arguments for the error message format
         */
        public InvalidCronExpression(final String errorMessage, final Object... stringFormatArgs) {

            super(String.format(errorMessage, stringFormatArgs));
        }

        /**
         * Build a new instance.
         *
         * @param causeException   The root exception
         * @param errorMessage     The error message
         * @param stringFormatArgs The arguments for the error message format
         */
        public InvalidCronExpression(final InvalidCronExpression causeException,
                                     final String errorMessage,
                                     final Object... stringFormatArgs) {

            super(String.format(errorMessage, stringFormatArgs), causeException);
        }
    }
}
