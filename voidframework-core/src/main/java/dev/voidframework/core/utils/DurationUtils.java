package dev.voidframework.core.utils;

import dev.voidframework.core.exception.DurationException;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Locale;
import java.util.Map;

/**
 * Utility methods to handle durations.
 */
public final class DurationUtils {

    private static final Map<String, TemporalUnit> TEMPORAL_UNIT_PER_NAME = Map.ofEntries(
        Map.entry("ns", ChronoUnit.NANOS),
        Map.entry("nano", ChronoUnit.NANOS),
        Map.entry("nanos", ChronoUnit.NANOS),
        Map.entry("nanosecond", ChronoUnit.NANOS),
        Map.entry("nanoseconds", ChronoUnit.NANOS),
        Map.entry("us", ChronoUnit.MICROS),
        Map.entry("micros", ChronoUnit.MICROS),
        Map.entry("micro", ChronoUnit.MICROS),
        Map.entry("microsecond", ChronoUnit.MICROS),
        Map.entry("microseconds", ChronoUnit.MICROS),
        Map.entry("ms", ChronoUnit.MILLIS),
        Map.entry("millis", ChronoUnit.MILLIS),
        Map.entry("milli", ChronoUnit.MILLIS),
        Map.entry("millisecond", ChronoUnit.MILLIS),
        Map.entry("milliseconds", ChronoUnit.MILLIS),
        Map.entry("s", ChronoUnit.SECONDS),
        Map.entry("second", ChronoUnit.SECONDS),
        Map.entry("seconds", ChronoUnit.SECONDS),
        Map.entry("m", ChronoUnit.MINUTES),
        Map.entry("minute", ChronoUnit.MINUTES),
        Map.entry("minutes", ChronoUnit.MINUTES),
        Map.entry("h", ChronoUnit.HOURS),
        Map.entry("hour", ChronoUnit.HOURS),
        Map.entry("hours", ChronoUnit.HOURS),
        Map.entry("d", ChronoUnit.DAYS),
        Map.entry("day", ChronoUnit.DAYS),
        Map.entry("days", ChronoUnit.DAYS));

    /**
     * Default constructor.
     */
    private DurationUtils() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Transforms a string expressing a duration to a {@code Duration}.
     * <p>For example:</p>
     * <blockquote><pre>
     *    DurationUtils.fromString("45 m");
     *    DurationUtils.fromString("45 minutes");
     * </pre></blockquote>
     * <p>The following temporal units can be used:</p>
     * <pre>
     * Family               Temporal unit
     * ----------           --------------------
     * Nanosecond           ns
     *                      nano
     *                      nanos
     *                      nanosecond
     *                      nanoseconds
     * Microsecond          us
     *                      micro
     *                      micros
     *                      microsecond
     *                      microseconds
     * Millisecond          ms
     *                      milli
     *                      millis
     *                      millisecond
     *                      milliseconds
     * Second               s
     *                      second
     *                      seconds
     * Minute               m
     *                      minute
     *                      minutes
     * Hour                 h
     *                      hour
     *                      hours
     * Day                  d
     *                      day
     *                      days
     * </pre>
     *
     * @param str String containing a duration
     * @return Duration obtained from the given argument
     */
    public static Duration fromString(final String str) {

        // Checks given arguments
        if (str == null) {
            return null;
        }

        int idx = 0;
        char c;
        final StringBuilder sbValue = new StringBuilder();
        final StringBuilder sbTemporalUnit = new StringBuilder();

        // Retrieves value (only numbers) as string
        for (; idx < str.length(); idx += 1) {
            c = str.charAt(idx);

            if (c >= '0' && c <= '9') {
                sbValue.append(c);
            } else if (c != ' ') {
                break;
            }
        }

        if (sbValue.isEmpty()) {
            throw new DurationException.MissingNumericValue();
        }

        // Retrieves temporal unit (only letter) as string
        for (; idx < str.length(); idx += 1) {
            c = str.charAt(idx);

            if (c >= 'A' && c <= 'z') {
                sbTemporalUnit.append(c);
            }
        }

        if (sbTemporalUnit.isEmpty()) {
            throw new DurationException.MissingTemporalUnit();
        }

        // Retrieves temporal unit
        final TemporalUnit temporalUnit = TEMPORAL_UNIT_PER_NAME.get(sbTemporalUnit.toString().toLowerCase(Locale.ENGLISH));
        if (temporalUnit == null) {
            throw new DurationException.InvalidTemporalUnit(sbTemporalUnit.toString());
        }

        try {
            // Retrieves numeric value
            final long value = Long.parseLong(sbValue.toString());

            // Creates Duration
            return Duration.of(value, temporalUnit);
        } catch (final NumberFormatException ex) {
            throw new DurationException.InvalidNumericValue(sbValue.toString(), ex);
        }
    }
}
