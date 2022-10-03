package dev.voidframework.scheduler.cron;

import dev.voidframework.core.helper.Reflection;
import dev.voidframework.scheduler.exception.SchedulerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class CronExpressionTest {

    @Test
    void badPartSingleValueTooHigh() {

        // Act
        Assertions.assertThrowsExactly(
            SchedulerException.InvalidCronExpression.class,
            () -> new CronExpression("125 12 10 2-5 * *"));
    }

    @Test
    void badPartListValueTooHigh() {

        // Act
        Assertions.assertThrowsExactly(
            SchedulerException.InvalidCronExpression.class,
            () -> new CronExpression("1,2,71,4 12 10 2-5 * *"));
    }

    @Test
    void badCronExpressionSize() {

        // Act
        Assertions.assertThrowsExactly(
            SchedulerException.InvalidCronExpression.class,
            () -> new CronExpression("1,2,71,4 * *"));
    }

    @Test
    void badCronExpressionTooManyWildcard() {

        // Act
        Assertions.assertThrowsExactly(
            SchedulerException.InvalidCronExpression.class,
            () -> new CronExpression("0 12 10 2-5 ** *"));
    }

    @Test
    void badCronExpressionDayOfWeek() {

        // Act
        Assertions.assertThrowsExactly(
            SchedulerException.InvalidCronExpression.class,
            () -> new CronExpression("0 0 18 0 0 sun"));
    }

    @Test
    void everySeconds() {

        // Arrange
        // Given: Monday, June 20, 2022 12:00:00
        // Expected: Monday, June 20, 2022 12:00:01
        final CronExpression cronExpression = new CronExpression("* * * * * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 20, 12, 0, 0);

        // Act
        final long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        // Assert
        Assertions.assertEquals(LocalDateTime.of(2022, 6, 20, 12, 0, 1), nextTriggerLocalDateTime);
    }

    @Test
    void everyMinutes() {

        // Arrange
        // Given: Monday, June 20, 2022 12:00:00
        // Expected: Monday, June 20, 2022 12:01:00
        final CronExpression cronExpression = new CronExpression("0 * * * * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 20, 12, 0, 0);

        // Act
        final long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        // Assert
        Assertions.assertEquals(LocalDateTime.of(2022, 6, 20, 12, 1, 0), nextTriggerLocalDateTime);
    }

    @Test
    void everyHours() {

        // Arrange
        // Given: Monday, June 20, 2022 12:00:00
        // Expected: Monday, June 20, 2022 13:00:00
        final CronExpression cronExpression = new CronExpression("0 0 * * * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 20, 12, 0, 0);

        // Act
        final long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        // Assert
        Assertions.assertEquals(LocalDateTime.of(2022, 6, 20, 13, 0, 0), nextTriggerLocalDateTime);
    }

    @Test
    void everyDays() {

        // Arrange
        // Given: Monday, June 20, 2022 12:00:00
        // Expected: Tuesday, June 21, 2022 12:00:00
        final CronExpression cronExpression = new CronExpression("0 0 0 * * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 20, 12, 0, 0);

        // Act
        final long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        // Assert
        Assertions.assertEquals(LocalDateTime.of(2022, 6, 21, 0, 0, 0), nextTriggerLocalDateTime);
    }

    @Test
    void everyDaysMonday() {

        // Arrange
        // Given: Monday, June 27, 2022 12:00:00
        // Expected: Monday, July 4, 2022 00:00:00
        final CronExpression cronExpression = new CronExpression("0 0 0 * * mon");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 27, 12, 0, 0);

        // Act
        final long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        // Assert
        Assertions.assertEquals(LocalDateTime.of(2022, 7, 4, 0, 0, 0), nextTriggerLocalDateTime);
    }

    @Test
    void everyMonths() {

        // Arrange
        // Given: Monday, June 20, 2022 12:00:00
        // Expected: Friday, July 1, 2022 00:00:00
        final CronExpression cronExpression = new CronExpression("0 0 0 1 * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 20, 12, 0, 0);

        // Act
        final long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        // Assert
        Assertions.assertEquals(LocalDateTime.of(2022, 7, 1, 0, 0, 0), nextTriggerLocalDateTime);
    }

    @Test
    void everyTwiceADayFromMondayToFridayFirstOccurrence2h30() {

        // Arrange
        // Twice a day (2h30 & 14h30) from Monday to Friday
        final CronExpression cronExpression = new CronExpression("0 30 2,14 * * 1-5");

        // Given: Friday, June 24, 2022 22:00:00
        // Expected: Monday, June 27, 2022 02:30:00
        LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 24, 22, 0, 0);

        // Act
        long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        // Assert
        Assertions.assertEquals(LocalDateTime.of(2022, 6, 27, 2, 30, 0), nextTriggerLocalDateTime);
    }

    @Test
    void everyTwiceADayFromMondayToFridaySecondOccurrence14h30() {

        // Arrange
        // Twice a day (2h30 & 14h30) from Monday to Friday
        final CronExpression cronExpression = new CronExpression("0 30 2,14 * * 1-5");

        // Given: Wednesday, June 22, 2022 12:00:00
        // Expected: Wednesday, June 22, 2022 14:30:00
        LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 22, 12, 0, 0);

        // Act
        long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        // Assert
        Assertions.assertEquals(LocalDateTime.of(2022, 6, 22, 14, 30, 0), nextTriggerLocalDateTime);
    }

    @Test
    void everySundayAt18h00() {

        // Arrange
        // Twice a day (2h30 & 14h30) from Monday to Friday
        final CronExpression cronExpression = new CronExpression("0 0 18 * * sun");

        // Given: Wednesday, June 22, 2022 12:00:00
        // Expected: Sunday, June 26, 2022 18:00:00
        LocalDateTime localDateTime = LocalDateTime.of(2022, 6, 22, 12, 0, 0);

        // Act
        long delay = callByReflection_getNextDelayMilliseconds(cronExpression, localDateTime);
        LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        // Assert
        Assertions.assertEquals(LocalDateTime.of(2022, 6, 26, 18, 0, 0), nextTriggerLocalDateTime);
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
