package dev.voidframework.core.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.charset.StandardCharsets;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class HexTest {

    @Test
    void toHexString() {

        // Arrange
        final String helloWorld = "Hello World!";

        // Act
        final String hex = Hex.toHex(helloWorld);

        // Assert
        Assertions.assertEquals("48656c6c6f20576f726c6421", hex);
    }

    @Test
    void toHexByteArray() {

        // Arrange
        final byte[] loremIpsum = "Lorem ipsum dolor sit amet".getBytes(StandardCharsets.UTF_8);

        // Act
        final String hex = Hex.toHex(loremIpsum);

        // Assert
        Assertions.assertEquals("4c6f72656d20697073756d20646f6c6f722073697420616d6574", hex);
    }

    @Test
    void toHexNullString() {

        // Arrange
        final String nullString = null;

        // Act
        final String hex = Hex.toHex(nullString);

        // Assert
        Assertions.assertNull(hex);
    }

    @Test
    void toHexNullByteArray() {

        // Arrange
        final byte[] nullByteArray = null;

        // Act
        final String hex = Hex.toHex(nullByteArray);

        // Assert
        Assertions.assertNull(hex);
    }
}
