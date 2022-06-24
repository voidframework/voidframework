package dev.voidframework.scheduler.cron;

import dev.voidframework.scheduler.exception.SchedulerException;

/**
 * CRON expression part representing a range (min, max).
 */
class CronExpressionPartRange extends CronExpressionPartStepValue {

    private final int min;
    private final int max;

    /**
     * Build a new instance.
     *
     * @param stepValue The step value
     * @param min       The min range value
     * @param max       The max range value
     */
    public CronExpressionPartRange(final int stepValue, final int min, final int max) {
        super(stepValue);

        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isNotCompliant(final int value) {
        return super.isNotCompliant(value) ^ (value >= this.min && value <= this.max);
    }

    @Override
    public void assertViolation(final int allowedMinStepValue,
                                final int allowedMaxStepValue,
                                final int allowedMinValue,
                                final int allowedMaxValue) {
        super.assertViolation(allowedMinStepValue, allowedMaxStepValue, allowedMinValue, allowedMaxValue);

        if ((this.min < allowedMinValue) || (this.max > allowedMaxValue)) {
            throw new SchedulerException.InvalidCronExpression(
                "Range value '%s..%s' is invalid. Allowed Range is '%s..%s'.",
                this.min,
                this.max,
                allowedMinValue,
                allowedMaxValue);
        }
        if (this.min > this.max) {
            throw new SchedulerException.InvalidCronExpression(
                "Min value '%s' can't be higher than Max value '%s'",
                this.min,
                this.max);
        }
    }
}
