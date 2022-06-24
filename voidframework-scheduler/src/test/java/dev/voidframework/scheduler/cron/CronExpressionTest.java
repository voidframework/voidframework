package dev.voidframework.scheduler.cron;

import dev.voidframework.core.helper.Reflection;
import dev.voidframework.scheduler.exception.SchedulerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;

@TestMethodOrder(MethodOrderer.MethodName.class)
public final class CronExpressionTest {

    @Test
    public void badPartSingleValueTooHigh() {
        Assertions.assertThrowsExactly(
            SchedulerException.InvalidCronExpression.class,
            () -> new CronExpression("125 12 10 2-5 * *"));
    }

    @Test
    public void badPartListValueTooHigh() {
        Assertions.assertThrowsExactly(
            SchedulerException.InvalidCronExpression.class,
            () -> new CronExpression("1,2,71,4 12 10 2-5 * *"));
    }

    @Test
    public void badCronExpressionSize() {
        Assertions.assertThrowsExactly(
            SchedulerException.InvalidCronExpression.class,
            () -> new CronExpression("1,2,71,4 * *"));
    }

    @Test
    public void badCronExpressionTooManyWildcard() {
        Assertions.assertThrowsExactly(
            SchedulerException.InvalidCronExpression.class,
            () -> new CronExpression("0 12 10 2-5 ** *"));
    }

    @Test
    public void everySeconds() {
        // Given: Monday, June 20, 2022 12:00:00
        // Expected: Monday, June 20, 2022 12:00:01
        final CronExpression cronExpression = new CronExpression("* * * * * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 20, 12, 0, 0);

        final long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        Assertions.assertEquals(LocalDateTime.of(2022, 6, 20, 12, 0, 1), nextTriggerLocalDateTime);
    }

    @Test
    public void everyMinutes() {
        // Given: Monday, June 20, 2022 12:00:00
        // Expected: Monday, June 20, 2022 12:01:00
        final CronExpression cronExpression = new CronExpression("0 * * * * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 20, 12, 0, 0);

        final long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        Assertions.assertEquals(LocalDateTime.of(2022, 6, 20, 12, 1, 0), nextTriggerLocalDateTime);
    }

    @Test
    public void everyHours() {
        // Given: Monday, June 20, 2022 12:00:00
        // Expected: Monday, June 20, 2022 13:00:00
        final CronExpression cronExpression = new CronExpression("0 0 * * * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 20, 12, 0, 0);

        final long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        Assertions.assertEquals(LocalDateTime.of(2022, 6, 20, 13, 0, 0), nextTriggerLocalDateTime);
    }

    @Test
    public void everyDays() {
        // Given: Monday, June 20, 2022 12:00:00
        // Expected: Tuesday, June 21, 2022 12:00:00
        final CronExpression cronExpression = new CronExpression("0 0 0 * * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 20, 12, 0, 0);

        final long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        Assertions.assertEquals(LocalDateTime.of(2022, 6, 21, 0, 0, 0), nextTriggerLocalDateTime);
    }

    @Test
    public void everyDaysMonday() {
        // Given: Monday, June 27, 2022 12:00:00
        // Expected: Monday, July 4, 2022 00:00:00
        final CronExpression cronExpression = new CronExpression("0 0 0 * * mon");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 27, 12, 0, 0);

        final long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        Assertions.assertEquals(LocalDateTime.of(2022, 7, 4, 0, 0, 0), nextTriggerLocalDateTime);
    }

    @Test
    public void everyMonths() {
        // Given: Monday, June 20, 2022 12:00:00
        // Expected: Friday, July 1, 2022 00:00:00
        final CronExpression cronExpression = new CronExpression("0 0 0 1 * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 20, 12, 0, 0);

        final long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        Assertions.assertEquals(LocalDateTime.of(2022, 7, 1, 0, 0, 0), nextTriggerLocalDateTime);
    }

    @Test
    public void everyTwiceADayFromMondayToFriday() {
        // Twice a day (2h30 & 14h30) from Monday to Friday
        final CronExpression cronExpression = new CronExpression("0 30 2,14 * * 1-5");

        // Given: Wednesday, June 22, 2022 12:00:00
        // Expected: Wednesday, June 22, 2022 14:30:00
        LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 22, 12, 0, 0);
        long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);
        Assertions.assertEquals(LocalDateTime.of(2022, 6, 22, 14, 30, 0), nextTriggerLocalDateTime);

        // Given: Friday, June 24, 2022 22:00:00
        // Expected: Monday, June 27, 2022 02:30:00
        localDateTime = LocalDateTime.of(2022, 6, 24, 22, 0, 0);
        delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);
        Assertions.assertEquals(LocalDateTime.of(2022, 6, 27, 2, 30, 0), nextTriggerLocalDateTime);
    }

    /**
     * Calls "getNextDelayMilliseconds" by using reflection.
     *
     * @param cronExpression The CRON expression instance
     * @param localDateTime  The method argument
     * @return The method call result
     */
    private long callByReflection_getNextDelayMilliseconds(final CronExpression cronExpression, final LocalDateTime localDateTime) {
        final Long delay = Reflection.callMethod(
            cronExpression,
            "getNextDelayMilliseconds",
            Long.class,
            new Class[]{LocalDateTime.class},
            localDateTime);
        Assertions.assertNotNull(delay);

        return delay;
    }
}
