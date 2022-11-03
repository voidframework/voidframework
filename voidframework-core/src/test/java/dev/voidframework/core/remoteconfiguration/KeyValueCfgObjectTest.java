package dev.voidframework.core.remoteconfiguration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class KeyValueCfgObjectTest {

    static Stream<Arguments> namedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("keyValueSimple", "value"), "KeyValueCfgObject{key=\"value\"}"),
            Arguments.of(Named.of("keyValueSimpleDouble", "12.45"), "KeyValueCfgObject{key=12.45}"),
            Arguments.of(Named.of("keyValueSimpleLong", "1337"), "KeyValueCfgObject{key=1337}"),
            Arguments.of(Named.of("keyValueQuoted", "\"value\""), "KeyValueCfgObject{key=\"value\"}"),
            Arguments.of(Named.of("keyValueQuotedDouble", "\"12.45\""), "KeyValueCfgObject{key=\"12.45\"}"),
            Arguments.of(Named.of("keyValueBoolean", "true"), "KeyValueCfgObject{key=true}"),
            Arguments.of(Named.of("keyValueQuotedBoolean", "\"true\""), "KeyValueCfgObject{key=\"true\"}"));
    }

    @ParameterizedTest
    @MethodSource("namedKeyValueArguments")
    void keyValueQuotedDouble(final String value, final String expected) {

        // Arrange
        final KeyValueCfgObject keyValueCfgObject = new KeyValueCfgObject("key", value);

        // Act
        final String toString = keyValueCfgObject.toString();

        // Assert
        Assertions.assertEquals(expected, toString);
    }

    @Test
    void toStringWithAdaptativeMaskNonSensitiveData() {

        // Arrange
        final KeyValueCfgObject keyValueCfgObject = new KeyValueCfgObject("key", "value");

        // Act
        final String toString = keyValueCfgObject.toStringWithAdaptativeMask();

        // Assert
        Assertions.assertEquals("KeyValueCfgObject{key=\"value\"}", toString);
    }

    @Test
    void toStringWithAdaptativeMaskSensitiveData() {

        // Arrange
        final KeyValueCfgObject keyValueCfgObject = new KeyValueCfgObject("token", "my-secret-token");

        // Act
        final String toString = keyValueCfgObject.toStringWithAdaptativeMask();

        // Assert
        Assertions.assertEquals("KeyValueCfgObject{token=**********}", toString);
    }

    @Test
    void apply() {

        // Arrange
        final StringBuilder stringBuilder = new StringBuilder(64);
        final KeyValueCfgObject keyValueCfgObject = new KeyValueCfgObject("token", "my-secret-token");

        // Act
        keyValueCfgObject.apply(stringBuilder);
        final String content = stringBuilder.toString().trim();

        // Assert
        Assertions.assertEquals("token = \"my-secret-token\"", content);
    }
}
