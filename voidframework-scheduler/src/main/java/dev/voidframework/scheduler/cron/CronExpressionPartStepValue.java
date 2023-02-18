package dev.voidframework.scheduler.cron;

import dev.voidframework.scheduler.exception.SchedulerException;

/**
 * CRON expression part representing a step value.
 *
 * @since 1.0.0
 */
class CronExpressionPartStepValue implements CronExpressionPart {

    private final int stepValue;

    /**
     * Build a new instance.
     *
     * @param stepValue The value
     * @since 1.0.0
     */
    protected CronExpressionPartStepValue(final int stepValue) {

        this.stepValue = stepValue;
    }

    @Override
    public boolean isNotCompliant(final int value) {

        return this.stepValue == -1 || value % this.stepValue == 0;
    }

    @Override
    public void assertViolation(final int allowedMinStepValue,
                                final int allowedMaxStepValue,
                                final int allowedMinValue,
                                final int allowedMaxValue) {

        if (this.stepValue != -1 && !(this.stepValue >= allowedMinStepValue && this.stepValue <= allowedMaxStepValue)) {
            throw new SchedulerException.InvalidCronExpression(
                "Step value '%s' is invalid. It must be between '%s..%s'.",
                this.stepValue,
                allowedMaxStepValue,
                allowedMinStepValue);
        }
    }
}
