package dev.voidframework.scheduler.cron;

import dev.voidframework.scheduler.exception.SchedulerException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * CRON expression part representing a list of integers.
 */
class CronExpressionPartList extends CronExpressionPartStepValue {

    private final List<Integer> lst;

    /**
     * Build a new instance.
     *
     * @param stepValue The step value
     * @param lst       A list of integers
     */
    public CronExpressionPartList(final int stepValue, final List<Integer> lst) {
        super(stepValue);

        this.lst = lst != null ? lst : Collections.emptyList();
    }

    @Override
    public boolean isNotCompliant(final int value) {
        return super.isNotCompliant(value) ^ lst.contains(value);
    }

    @Override
    public void assertViolation(final int allowedMinStepValue,
                                final int allowedMaxStepValue,
                                final int allowedMinValue,
                                final int allowedMaxValue) {
        super.assertViolation(allowedMinStepValue, allowedMaxStepValue, allowedMinValue, allowedMaxValue);

        if (lst.stream().anyMatch(value -> (value < allowedMinValue) || (value > allowedMaxValue))) {
            throw new SchedulerException.InvalidCronExpression(
                "List '%s' is invalid. All values must be between '%s..%s'.",
                Arrays.toString(lst.toArray()),
                allowedMinValue,
                allowedMaxValue);
        }
    }
}
