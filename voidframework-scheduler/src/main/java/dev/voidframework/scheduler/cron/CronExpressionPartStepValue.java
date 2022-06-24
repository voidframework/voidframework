package dev.voidframework.scheduler.cron;

import dev.voidframework.scheduler.exception.SchedulerException;

/**
 * CRON expression part representing a step value.
 */
class CronExpressionPartStepValue implements CronExpressionPart {

    private final int stepValue;

    /**
     * Build a new instance.
     *
     * @param stepValue The value
     */
    protected CronExpressionPartStepValue(final int stepValue) {
        this.stepValue = stepValue;
    }

    @Override
    public boolean isNotCompliant(final int value) {
        return stepValue == -1 || value % stepValue == 0;
    }

    @Override
    public void assertViolation(final int allowedMinStepValue,
                                final int allowedMaxStepValue,
                                final int allowedMinValue,
                                final int allowedMaxValue) {
        if (stepValue != -1) {
            if (!(stepValue >= allowedMinStepValue && stepValue <= allowedMaxStepValue)) {
                throw new SchedulerException.InvalidCronExpression(
                    "Step value '%s' is invalid. It must be between '%s..%s'.",
                    stepValue,
                    allowedMaxStepValue,
                    allowedMinStepValue);
            }
        }
    }
}
