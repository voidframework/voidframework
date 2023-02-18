package dev.voidframework.scheduler.cron;

import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.scheduler.exception.SchedulerException;
import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A CRON expression.
 *
 * @since 1.0.0
 */
public class CronExpression {

    private static final Pattern REGEXP_PATTERN_RANGE = Pattern.compile("^([^-]+)-([^-/]+)(/(\\d+))?$");
    private static final Pattern REGEXP_PATTERN_LIST = Pattern.compile("^([^-/]+)(/(\\d+))?$");
    private static final Pattern REGEXP_PATTERN_SINGLE = Pattern.compile("^(\\d+)(/(\\d+))?$");

    private static final List<String> WILDCARD_REPLACEMENT = List.of(
        "0-59",  // Second
        "0-59",  // Minute
        "0-23",  // Hour
        "1-31",  // Day of Month
        "1-12",  // Month
        "0-6");  // Day of Week

    private static final List<Consumer<CronExpressionPart>> CRON_EXPRESSION_PART_VALIDATOR = List.of(
        (c) -> c.assertViolation(1, 60, 0, 59),  // Second
        (c) -> c.assertViolation(1, 60, 0, 59),  // Minute
        (c) -> c.assertViolation(1, 24, 0, 23),  // Hour
        (c) -> c.assertViolation(1, 32, 1, 31),  // Day of Month
        (c) -> c.assertViolation(1, 13, 1, 12),  // Month
        (c) -> c.assertViolation(1, 7, 0, 6));   // Day of Week

    private static final int IDX_SECOND = 0;
    private static final int IDX_MINUTE = 1;
    private static final int IDX_HOUR = 2;
    private static final int IDX_DAY_OF_MONTH = 3;
    private static final int IDX_MONTH = 4;
    private static final int IDX_DAY_OF_WEEK = 5;

    private static final Map<String, String> MONTH_NAME_TO_VALUE = Map.ofEntries(
        Map.entry("JAN", "1"),
        Map.entry("FEB", "2"),
        Map.entry("MAR", "3"),
        Map.entry("APR", "4"),
        Map.entry("MAY", "5"),
        Map.entry("JUN", "6"),
        Map.entry("JUL", "7"),
        Map.entry("AUG", "8"),
        Map.entry("SEP", "9"),
        Map.entry("OCT", "10"),
        Map.entry("NOV", "11"),
        Map.entry("DEC", "12"));

    private static final Map<String, String> DAY_OF_WEEK_NAME_TO_VALUE = Map.ofEntries(
        Map.entry("SUN", "0"),
        Map.entry("MON", "1"),
        Map.entry("TUE", "2"),
        Map.entry("WED", "3"),
        Map.entry("THU", "4"),
        Map.entry("FRI", "5"),
        Map.entry("SAT", "6"));

    private static final Map<DayOfWeek, Integer> DAY_OF_WEEK_CRON_VALUE = Map.ofEntries(
        Map.entry(DayOfWeek.SUNDAY, 0),
        Map.entry(DayOfWeek.MONDAY, 1),
        Map.entry(DayOfWeek.TUESDAY, 2),
        Map.entry(DayOfWeek.WEDNESDAY, 3),
        Map.entry(DayOfWeek.THURSDAY, 4),
        Map.entry(DayOfWeek.FRIDAY, 5),
        Map.entry(DayOfWeek.SATURDAY, 6));

    private final CronExpressionPart[] cronExpressionPartArray;

    /**
     * Build a new instance.
     *
     * @param cron The CRON expression to parse
     * @since 1.0.0
     */
    public CronExpression(final String cron) {

        // ┌───────────── second (0 - 59)
        // │ ┌───────────── minute (0 - 59)
        // │ │ ┌───────────── hour (0 - 23)
        // │ │ │ ┌───────────── day of the month (1 - 31)
        // │ │ │ │ ┌───────────── month (1 - 12 / jan - dec)
        // │ │ │ │ │ ┌───────────── day of the week (0 - 6 / sun - sat) (Sunday to Saturday)
        // │ │ │ │ │ │
        // │ │ │ │ │ │
        // * * * * * *

        // Prepares the CRON expression
        final String[] cronArray = cron.toUpperCase(Locale.ENGLISH).split(StringUtils.SPACE);
        if (cronArray.length < 5) {
            throw new SchedulerException.InvalidCronExpression("CRON expression is invalid '%s'", cron);
        }

        for (final Map.Entry<String, String> entrySet : DAY_OF_WEEK_NAME_TO_VALUE.entrySet()) {
            cronArray[IDX_DAY_OF_WEEK] = cronArray[IDX_DAY_OF_WEEK].replace(entrySet.getKey(), entrySet.getValue());
        }

        for (final Map.Entry<String, String> entrySet : MONTH_NAME_TO_VALUE.entrySet()) {
            cronArray[IDX_MONTH] = cronArray[IDX_MONTH].replace(entrySet.getKey(), entrySet.getValue());
        }

        // Parses the prepared CRON expression
        this.cronExpressionPartArray = new CronExpressionPart[6];
        for (int idx = 0; idx < cronExpressionPartArray.length; ++idx) {
            // Standardize CRON expression
            final String standardizedPart = cronArray[idx]
                .replace(StringConstants.QUESTION_MARK, StringConstants.WILDCARD)
                .replace(StringConstants.WILDCARD, WILDCARD_REPLACEMENT.get(idx));

            // Parse and validate each part
            try {
                this.cronExpressionPartArray[idx] = this.parseCronExpressionPart(standardizedPart);
                if (idx < CRON_EXPRESSION_PART_VALIDATOR.size()) {
                    CRON_EXPRESSION_PART_VALIDATOR.get(idx).accept(cronExpressionPartArray[idx]);
                }
            } catch (final SchedulerException.InvalidCronExpression ex) {
                throw new SchedulerException.InvalidCronExpression(
                    ex,
                    "Can't use CRON '%s', error with the part #%d '%s'",
                    cron,
                    idx + 1,
                    cronArray[idx]);
            }
        }
    }

    /**
     * Retrieves the delay from now to the next match.
     *
     * @param zoneId The Zone to use for manipulating datetime
     * @return The next delay in milliseconds
     * @since 1.0.0
     */
    public long getNextDelayMilliseconds(final ZoneId zoneId) {

        return getNextDelayMilliseconds(LocalDateTime.now(zoneId));
    }

    /**
     * Retrieves the delay from given datetime to the next match.
     *
     * @param from The datetime
     * @return The next delay in milliseconds
     * @since 1.0.0
     */
    private long getNextDelayMilliseconds(final LocalDateTime from) {

        LocalDateTime nextTrigger = from.plusSeconds(1);

        CronExpressionPart cronExpressionPart = cronExpressionPartArray[IDX_SECOND];
        while (cronExpressionPart.isNotCompliant(nextTrigger.getSecond())) {
            nextTrigger = nextTrigger.plusSeconds(1);
        }

        cronExpressionPart = cronExpressionPartArray[IDX_MINUTE];
        while (cronExpressionPart.isNotCompliant(nextTrigger.getMinute())) {
            nextTrigger = nextTrigger.plusMinutes(1);
        }

        cronExpressionPart = cronExpressionPartArray[IDX_HOUR];
        while (cronExpressionPart.isNotCompliant(nextTrigger.getHour())) {
            nextTrigger = nextTrigger.plusHours(1);
        }

        cronExpressionPart = cronExpressionPartArray[IDX_DAY_OF_MONTH];
        while (cronExpressionPart.isNotCompliant(nextTrigger.getDayOfMonth())) {
            nextTrigger = nextTrigger.plusDays(1);
        }

        cronExpressionPart = cronExpressionPartArray[IDX_MONTH];
        while (cronExpressionPart.isNotCompliant(nextTrigger.getMonthValue())) {
            nextTrigger = nextTrigger.plusMonths(1);
        }

        cronExpressionPart = cronExpressionPartArray[IDX_DAY_OF_WEEK];
        while (cronExpressionPart.isNotCompliant(DAY_OF_WEEK_CRON_VALUE.get(nextTrigger.getDayOfWeek()))) {
            nextTrigger = nextTrigger.plusDays(1);
        }

        final int millisecondsToRemove = from.get(ChronoField.MILLI_OF_SECOND);
        return ChronoUnit.MILLIS.between(from.truncatedTo(ChronoUnit.MILLIS), nextTrigger.minus(millisecondsToRemove, ChronoUnit.MILLIS));
    }

    /**
     * Parses a string into {@code CronExpressionPart}.
     *
     * @param str String to parse
     * @return A {@code CronExpressionPart} retrieve from parsed string
     * @since 1.0.0
     */
    private CronExpressionPart parseCronExpressionPart(final String str) {

        CronExpressionPart cronExpressionPart = null;

        if (str.contains(StringConstants.HYPHEN)) {
            final Matcher matcher = REGEXP_PATTERN_RANGE.matcher(str);
            if (matcher.find()) {
                final int minRange = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
                final int maxRange = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : minRange;
                final int stepValue = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : -1;

                cronExpressionPart = new CronExpressionPartRange(stepValue, minRange, maxRange);
            }
        } else if (str.contains(StringConstants.COMMA)) {
            final Matcher matcher = REGEXP_PATTERN_LIST.matcher(str);
            if (matcher.find()) {
                final String listValue = matcher.group(1);
                final int stepValue = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : -1;

                cronExpressionPart = new CronExpressionPartList(
                    stepValue,
                    Arrays.stream(listValue.split(StringConstants.COMMA)).map(Integer::parseInt).collect(Collectors.toList()));
            }
        } else {
            final Matcher matcher = REGEXP_PATTERN_SINGLE.matcher(str);
            if (matcher.find()) {
                final int singleValue = Integer.parseInt(matcher.group(1));
                final int stepValue = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : -1;

                cronExpressionPart = new CronExpressionPartRange(stepValue, singleValue, singleValue);
            }
        }

        if (cronExpressionPart == null) {
            throw new SchedulerException.InvalidCronExpression("Can't parse CRON expression part: %s", str);
        }

        return cronExpressionPart;
    }
}
