package dev.voidframework.core.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
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
import java.time.temporal.TemporalAmount;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class ConfigurationUtilsTest {

    private static Stream<Arguments> getBooleanOrDefaultNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "boolean.value = true"), false),
            Arguments.of(Named.of("With existing null value", "boolean.value = null"), true),
            Arguments.of(Named.of("With non existing value", ""), true));
    }

    private static Stream<Arguments> getBooleanOrFallbackNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "boolean.value = true\nboolean.valueFallback = false")),
            Arguments.of(Named.of("With existing null value", "boolean.value = null\nboolean.valueFallback = true")),
            Arguments.of(Named.of("With non existing value", "boolean.valueFallback = true")));
    }

    private static Stream<Arguments> getBytesOrDefaultNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "bytes.value = 64M"), 1024, 67108864),
            Arguments.of(Named.of("With existing null value", "bytes.value = null"), 1024, 1024),
            Arguments.of(Named.of("With non existing value", ""), 1024, 1024));
    }

    private static Stream<Arguments> getBytesOrFallbackNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "bytes.value = 64M\nbytes.valueFallback = 32M"), 67108864),
            Arguments.of(Named.of("With existing null value", "bytes.value = null\nbytes.valueFallback = 32M"), 33554432),
            Arguments.of(Named.of("With non existing value", "bytes.valueFallback = 32M"), 33554432));
    }

    private static Stream<Arguments> getDurationOrDefaultWithTimeUnitNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "duration.value = 2 minutes"), 1, 2),
            Arguments.of(Named.of("With existing null value", "duration.value = null"), 1, 1),
            Arguments.of(Named.of("With non existing value", ""), 1, 1));
    }

    private static Stream<Arguments> getDurationOrFallbackWithTimeUnitNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "duration.value = 2 minutes\nduration.valueFallback = 5 minutes"), 2),
            Arguments.of(Named.of("With existing null value", "duration.value = null\nduration.valueFallback = 5 minutes"), 5),
            Arguments.of(Named.of("With non existing value", "duration.valueFallback = 5 minutes"), 5));
    }

    private static Stream<Arguments> getDurationOrDefaultNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "duration.value = 2 minutes"), Duration.ofMinutes(1), Duration.ofMinutes(2)),
            Arguments.of(Named.of("With existing null value", "duration.value = null"), Duration.ofMinutes(1), Duration.ofMinutes(1)),
            Arguments.of(Named.of("With non existing value", ""), Duration.ofMinutes(1), Duration.ofMinutes(1)));
    }

    private static Stream<Arguments> getDurationOrFallbackNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "duration.value = 2 minutes\nduration.valueFallback = 5 minutes"), Duration.ofMinutes(2)),
            Arguments.of(Named.of("With existing null value", "duration.value = null\nduration.valueFallback = 5 minutes"), Duration.ofMinutes(5)),
            Arguments.of(Named.of("With non existing value", "duration.valueFallback = 5 minutes"), Duration.ofMinutes(5)));
    }

    private static Stream<Arguments> getEnumOrDefaultNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "enum.value = ON"), State.OFF, State.ON),
            Arguments.of(Named.of("With existing null value", "enum.value = null"), State.OFF, State.OFF),
            Arguments.of(Named.of("With non existing value", ""), State.OFF, State.OFF));
    }

    private static Stream<Arguments> getEnumOrFallbackNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "enum.value = ON\nenum.valueFallback = OFF"), State.ON),
            Arguments.of(Named.of("With existing null value", "enum.value = null\nenum.valueFallback = OFF"), State.OFF),
            Arguments.of(Named.of("With non existing value", "enum.valueFallback = OFF"), State.OFF));
    }

    private static Stream<Arguments> getIntOrDefaultNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "int.value = 2"), 1, 2),
            Arguments.of(Named.of("With existing null value", "int.value = null"), 1, 1),
            Arguments.of(Named.of("With non existing value", ""), 1, 1));
    }

    private static Stream<Arguments> getIntOrFallbackNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "int.value = 2\nint.valueFallback = 5"), 2),
            Arguments.of(Named.of("With existing null value", "int.value = null\nint.valueFallback = 5"), 5),
            Arguments.of(Named.of("With non existing value", "int.valueFallback = 5"), 5));
    }

    private static Stream<Arguments> getLongOrDefaultNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "long.value = 2"), 1, 2),
            Arguments.of(Named.of("With existing null value", "long.value = null"), 1, 1),
            Arguments.of(Named.of("With non existing value", ""), 1, 1));
    }

    private static Stream<Arguments> getLongOrFallbackNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "long.value = 2\nlong.valueFallback = 5"), 2),
            Arguments.of(Named.of("With existing null value", "long.value = null\nlong.valueFallback = 5"), 5),
            Arguments.of(Named.of("With non existing value", "long.valueFallback = 5"), 5));
    }

    private static Stream<Arguments> getStringOrDefaultNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "string.value = Apple"), "Pear", "Apple"),
            Arguments.of(Named.of("With existing null value", "string.value = null"), "Pear", "Pear"),
            Arguments.of(Named.of("With non existing value", ""), "Pear", "Pear"));
    }

    private static Stream<Arguments> getStringOrFallbackNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "string.value = Apple\nstring.valueFallback = Pear"), "Apple"),
            Arguments.of(Named.of("With existing null value", "string.value = null\nstring.valueFallback = Pear"), "Pear"),
            Arguments.of(Named.of("With non existing value", "string.valueFallback = Pear"), "Pear"));
    }

    private static Stream<Arguments> getTemporalOrDefaultNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "temporal.value = 2 minutes"), Duration.ofMinutes(1), Duration.ofMinutes(2)),
            Arguments.of(Named.of("With existing null value", "temporal.value = null"), Duration.ofMinutes(1), Duration.ofMinutes(1)),
            Arguments.of(Named.of("With non existing value", ""), Duration.ofMinutes(1), Duration.ofMinutes(1)));
    }

    private static Stream<Arguments> getTemporalOrFallbackNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("With existing value", "temporal.value = 2 minutes\ntemporal.valueFallback = 5 minutes"), Duration.ofMinutes(2)),
            Arguments.of(Named.of("With existing null value", "temporal.value = null\ntemporal.valueFallback = 5 minutes"), Duration.ofMinutes(5)),
            Arguments.of(Named.of("With non existing value", "temporal.valueFallback = 5 minutes"), Duration.ofMinutes(5)));
    }

    @Test
    void getAllRootLevelPaths() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("""
            basepath.value1.key = "Hello"
            basepath {
                value2 {
                    key = "World"
                }

                value3.key = "!"
            }
            """);

        // Act
        final Set<String> pathSet = ConfigurationUtils.getAllRootLevelPaths(configuration, "basepath");

        // Assert
        Assertions.assertNotNull(pathSet);
        Assertions.assertEquals(3, pathSet.size());
        Assertions.assertTrue(pathSet.contains("value1"));
        Assertions.assertTrue(pathSet.contains("value2"));
        Assertions.assertTrue(pathSet.contains("value3"));
    }

    @Test
    void constructor() throws NoSuchMethodException {

        // Act
        final Constructor<ConfigurationUtils> constructor = ConfigurationUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);

        // Assert
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertEquals("This is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @ParameterizedTest
    @MethodSource("getBooleanOrDefaultNamedKeyValueArguments")
    void getBooleanOrDefault(final String configurationAsString, final boolean defaultValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final boolean value = ConfigurationUtils.getBooleanOrDefault(configuration, "boolean.value", defaultValue);

        // Assert
        Assertions.assertTrue(value);
    }

    @ParameterizedTest
    @MethodSource("getBooleanOrFallbackNamedKeyValueArguments")
    void getBooleanOrFallback(final String configurationAsString) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final boolean value = ConfigurationUtils.getBooleanOrFallback(configuration, "boolean.value", "boolean.valueFallback");

        // Assert
        Assertions.assertTrue(value);
    }

    @ParameterizedTest
    @MethodSource("getBytesOrDefaultNamedKeyValueArguments")
    void getBytesOrDefault(final String configurationAsString, final long defaultValue, final long expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final long value = ConfigurationUtils.getBytesOrDefault(configuration, "bytes.value", defaultValue);

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getBytesOrFallbackNamedKeyValueArguments")
    void getBytesOrFallback(final String configurationAsString, final long expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final long value = ConfigurationUtils.getBytesOrFallback(configuration, "bytes.value", "bytes.valueFallback");

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getDurationOrDefaultWithTimeUnitNamedKeyValueArguments")
    void getDurationOrDefaultWithTimeUnit(final String configurationAsString, final long defaultValue, final long expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final long value = ConfigurationUtils.getDurationOrDefault(configuration, "duration.value", TimeUnit.MINUTES, defaultValue);

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getDurationOrFallbackWithTimeUnitNamedKeyValueArguments")
    void getDurationOrFallbackWithTimeUnit(final String configurationAsString, final long expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final long value = ConfigurationUtils.getDurationOrFallback(configuration, "duration.value", TimeUnit.MINUTES, "duration.valueFallback");

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getDurationOrDefaultNamedKeyValueArguments")
    void getDurationOrDefault(final String configurationAsString, final Duration defaultValue, final Duration expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final Duration value = ConfigurationUtils.getDurationOrDefault(configuration, "duration.value", defaultValue);

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getDurationOrFallbackNamedKeyValueArguments")
    void getDurationOrFallback(final String configurationAsString, final Duration expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final Duration value = ConfigurationUtils.getDurationOrFallback(configuration, "duration.value", "duration.valueFallback");

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getEnumOrDefaultNamedKeyValueArguments")
    void getEnumOrDefault(final String configurationAsString, final State defaultValue, final State expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final State value = ConfigurationUtils.getEnumOrDefault(configuration, "enum.value", State.class, defaultValue);

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getEnumOrFallbackNamedKeyValueArguments")
    void getEnumOrFallback(final String configurationAsString, final State expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final State value = ConfigurationUtils.getEnumOrFallback(configuration, "enum.value", State.class, "enum.valueFallback");

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getIntOrDefaultNamedKeyValueArguments")
    void getIntOrDefault(final String configurationAsString, final int defaultValue, final int expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final int value = ConfigurationUtils.getIntOrDefault(configuration, "int.value", defaultValue);

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getIntOrFallbackNamedKeyValueArguments")
    void getIntOrFallback(final String configurationAsString, final int expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final int value = ConfigurationUtils.getIntOrFallback(configuration, "int.value", "int.valueFallback");

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getLongOrDefaultNamedKeyValueArguments")
    void getLongOrDefault(final String configurationAsString, final long defaultValue, final long expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final long value = ConfigurationUtils.getLongOrDefault(configuration, "long.value", defaultValue);

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getLongOrFallbackNamedKeyValueArguments")
    void getLongOrFallback(final String configurationAsString, final long expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final long value = ConfigurationUtils.getLongOrFallback(configuration, "long.value", "long.valueFallback");

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getStringOrDefaultNamedKeyValueArguments")
    void getStringOrDefault(final String configurationAsString, final String defaultValue, final String expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final String value = ConfigurationUtils.getStringOrDefault(configuration, "string.value", defaultValue);

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getStringOrFallbackNamedKeyValueArguments")
    void getStringOrFallback(final String configurationAsString, final String expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final String value = ConfigurationUtils.getStringOrFallback(configuration, "string.value", "string.valueFallback");

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getTemporalOrDefaultNamedKeyValueArguments")
    void getTemporalOrDefault(final String configurationAsString, final TemporalAmount defaultValue, final TemporalAmount expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final TemporalAmount value = ConfigurationUtils.getTemporalOrDefault(configuration, "temporal.value", defaultValue);

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    @ParameterizedTest
    @MethodSource("getTemporalOrFallbackNamedKeyValueArguments")
    void getTemporalOrFallback(final String configurationAsString, final TemporalAmount expectedValue) {

        // Arrange
        final Config configuration = ConfigFactory.parseString(configurationAsString);

        // Act
        final TemporalAmount value = ConfigurationUtils.getTemporalOrFallback(configuration, "temporal.value", "temporal.valueFallback");

        // Assert
        Assertions.assertEquals(expectedValue, value);
    }

    public enum State {

        ON,
        OFF
    }
}
