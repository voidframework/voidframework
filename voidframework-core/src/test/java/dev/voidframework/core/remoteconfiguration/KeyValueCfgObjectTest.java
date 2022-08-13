package dev.voidframework.core.remoteconfiguration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class KeyValueCfgObjectTest {

    @Test
    public void keyValueSimple() {

        // Arrange
        final KeyValueCfgObject keyValueCfgObject = new KeyValueCfgObject("key", "value");

        // Act
        final String toString = keyValueCfgObject.toString();

        // Assert
        Assertions.assertEquals("KeyValueCfgObject[key <- \"value\"]", toString);
    }

    @Test
    public void keyValueSimpleDouble() {

        // Arrange
        final KeyValueCfgObject keyValueCfgObject = new KeyValueCfgObject("key", "12.45");

        // Act
        final String toString = keyValueCfgObject.toString();

        // Assert
        Assertions.assertEquals("KeyValueCfgObject[key <- 12.45]", toString);
    }

    @Test
    public void keyValueSimpleLong() {

        // Arrange
        final KeyValueCfgObject keyValueCfgObject = new KeyValueCfgObject("key", "1337");

        // Act
        final String toString = keyValueCfgObject.toString();

        // Assert
        Assertions.assertEquals("KeyValueCfgObject[key <- 1337]", toString);
    }

    @Test
    public void keyValueQuoted() {

        // Arrange
        final KeyValueCfgObject keyValueCfgObject = new KeyValueCfgObject("key", "\"value\"");

        // Act
        final String toString = keyValueCfgObject.toString();

        // Assert
        Assertions.assertEquals("KeyValueCfgObject[key <- \"value\"]", toString);
    }

    @Test
    public void keyValueQuotedDouble() {

        // Arrange
        final KeyValueCfgObject keyValueCfgObject = new KeyValueCfgObject("key", "\"12.45\"");

        // Act
        final String toString = keyValueCfgObject.toString();

        // Assert
        Assertions.assertEquals("KeyValueCfgObject[key <- \"12.45\"]", toString);
    }

    @Test
    public void toStringWithAdaptativeMaskNonSensitiveData() {

        // Arrange
        final KeyValueCfgObject keyValueCfgObject = new KeyValueCfgObject("key", "value");

        // Act
        final String toString = keyValueCfgObject.toStringWithAdaptativeMask();

        // Assert
        Assertions.assertEquals("KeyValueCfgObject[key <- \"value\"]", toString);
    }

    @Test
    public void toStringWithAdaptativeMaskSensitiveData() {

        // Arrange
        final KeyValueCfgObject keyValueCfgObject = new KeyValueCfgObject("token", "my-secret-token");

        // Act
        final String toString = keyValueCfgObject.toStringWithAdaptativeMask();

        // Assert
        Assertions.assertEquals("KeyValueCfgObject[token <- **********]", toString);
    }

    @Test
    public void apply() {

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
