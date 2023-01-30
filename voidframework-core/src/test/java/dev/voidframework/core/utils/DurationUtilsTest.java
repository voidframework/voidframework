package dev.voidframework.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class DurationUtilsTest {

    static Stream<Arguments> fromStringInvalidDurationArguments() {
        return Stream.of(
            Arguments.of(Named.of("empty", ""), "Duration must start with a numeric value"),
            Arguments.of(Named.of("missing temporal unit", "45"), "Duration must end with a temporal unit"),
            Arguments.of(Named.of("invalid value", "days"), "Duration must start with a numeric value"),
            Arguments.of(Named.of("invalid temporal unit", "0 pear"), "'pear' is not a valid temporal unit"),
            Arguments.of(Named.of("invalid value & temporal unit", "pear"), "Duration must start with a numeric value"));
    }

    static Stream<Arguments> fromStringValidDurationArguments() {
        return Stream.of(
            Arguments.of(Named.of("ns", "159 ns"), Duration.of(159, ChronoUnit.NANOS)),
            Arguments.of(Named.of("nano", "1 nano"), Duration.of(1, ChronoUnit.NANOS)),
            Arguments.of(Named.of("nanos", "160 nanos"), Duration.of(160, ChronoUnit.NANOS)),
            Arguments.of(Named.of("nanosecond", "1 nanosecond"), Duration.of(1, ChronoUnit.NANOS)),
            Arguments.of(Named.of("nanoseconds", "161 nanoseconds"), Duration.of(161, ChronoUnit.NANOS)),
            Arguments.of(Named.of("us", "159 us"), Duration.of(159, ChronoUnit.MICROS)),
            Arguments.of(Named.of("micros", "160 micros"), Duration.of(160, ChronoUnit.MICROS)),
            Arguments.of(Named.of("micro", "1 micro"), Duration.of(1, ChronoUnit.MICROS)),
            Arguments.of(Named.of("microsecond", "1 microsecond"), Duration.of(1, ChronoUnit.MICROS)),
            Arguments.of(Named.of("microseconds", "161 microseconds"), Duration.of(161, ChronoUnit.MICROS)),
            Arguments.of(Named.of("ms", "159 ms"), Duration.of(159, ChronoUnit.MILLIS)),
            Arguments.of(Named.of("millis", "160 millis"), Duration.of(160, ChronoUnit.MILLIS)),
            Arguments.of(Named.of("milli", "1 milli"), Duration.of(1, ChronoUnit.MILLIS)),
            Arguments.of(Named.of("millisecond", "1 millisecond"), Duration.of(1, ChronoUnit.MILLIS)),
            Arguments.of(Named.of("milliseconds", "161 milliseconds"), Duration.of(161, ChronoUnit.MILLIS)),
            Arguments.of(Named.of("s", "45 s"), Duration.of(45, ChronoUnit.SECONDS)),
            Arguments.of(Named.of("second", "1 second"), Duration.of(1, ChronoUnit.SECONDS)),
            Arguments.of(Named.of("seconds", "46 seconds"), Duration.of(46, ChronoUnit.SECONDS)),
            Arguments.of(Named.of("m", "45 m"), Duration.of(45, ChronoUnit.MINUTES)),
            Arguments.of(Named.of("minute", "1 minute"), Duration.of(1, ChronoUnit.MINUTES)),
            Arguments.of(Named.of("minutes", "46 minutes"), Duration.of(46, ChronoUnit.MINUTES)),
            Arguments.of(Named.of("h", "15 h"), Duration.of(15, ChronoUnit.HOURS)),
            Arguments.of(Named.of("hour", "1 hour"), Duration.of(1, ChronoUnit.HOURS)),
            Arguments.of(Named.of("hours", "16 hours"), Duration.of(16, ChronoUnit.HOURS)),
            Arguments.of(Named.of("d", "24 d"), Duration.of(24, ChronoUnit.DAYS)),
            Arguments.of(Named.of("day", "1 day"), Duration.of(1, ChronoUnit.DAYS)),
            Arguments.of(Named.of("days", "25 days"), Duration.of(25, ChronoUnit.DAYS)),
            Arguments.of(Named.of("with spaces", "     2       h       "), Duration.of(2, ChronoUnit.HOURS)),
            Arguments.of(Named.of("without spaces", "2h"), Duration.of(2, ChronoUnit.HOURS)),
            Arguments.of(Named.of("uppercase & lowercase", "2 HOurS"), Duration.of(2, ChronoUnit.HOURS)));
    }

    @Test
    void constructor() throws NoSuchMethodException {

        // Act
        final Constructor<DurationUtils> constructor = DurationUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);

        // Assert
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertEquals("This is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    void fromStringNullValue() {

        // Act
        final Duration duration = DurationUtils.fromString(null);

        // Assert
        Assertions.assertNull(duration);
    }

    @ParameterizedTest
    @MethodSource("fromStringInvalidDurationArguments")
    void fromStringInvalidDuration(final String given, final String expectedExceptionMessage) {

        // Act
        final RuntimeException exception = Assertions.assertThrows(
            RuntimeException.class,
            () -> DurationUtils.fromString(given));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("fromStringValidDurationArguments")
    void fromStringValidDuration(final String given, final Duration expected) {

        // Act
        final Duration duration = DurationUtils.fromString(given);

        // Assert
        Assertions.assertEquals(expected, duration);
    }
}
