package dev.voidframework.scheduler.cron;

/**
 * Represents a subpart of a CRON expression.
 */
public interface CronExpressionPart {

    /**
     * Checks that the value does not match
     *
     * @param value The value to test
     * @return {@code true} if not compliant, otherwise, {@code false}
     */
    boolean isNotCompliant(final int value);

    /**
     * Checks that CRON expression has valid attributes.
     *
     * @param allowedMinStepValue The minimum value for the Step value
     * @param allowedMaxStepValue The maximum value for the Step value
     * @param allowedMinValue     The minimum value
     * @param allowedMaxValue     The maximum value
     */
    void assertViolation(final int allowedMinStepValue,
                         final int allowedMaxStepValue,
                         final int allowedMinValue,
                         final int allowedMaxValue);
}
