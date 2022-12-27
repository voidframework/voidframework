package dev.voidframework.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class HexUtilsTest {

    @Test
    void constructor() throws NoSuchMethodException {

        // Act
        final Constructor<HexUtils> constructor = HexUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);

        // Assert
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertEquals("This is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    void toHexString() {

        // Arrange
        final String helloWorld = "Hello World!";

        // Act
        final String hex = HexUtils.toHex(helloWorld);

        // Assert
        Assertions.assertEquals("48656c6c6f20576f726c6421", hex);
    }

    @Test
    void toHexByteArray() {

        // Arrange
        final byte[] loremIpsum = "Lorem ipsum dolor sit amet".getBytes(StandardCharsets.UTF_8);

        // Act
        final String hex = HexUtils.toHex(loremIpsum);

        // Assert
        Assertions.assertEquals("4c6f72656d20697073756d20646f6c6f722073697420616d6574", hex);
    }

    @Test
    void toHexNullString() {

        // Arrange
        final String nullString = null;

        // Act
        final String hex = HexUtils.toHex(nullString);

        // Assert
        Assertions.assertNull(hex);
    }

    @Test
    void toHexNullByteArray() {

        // Arrange
        final byte[] nullByteArray = null;

        // Act
        final String hex = HexUtils.toHex(nullByteArray);

        // Assert
        Assertions.assertNull(hex);
    }
}
