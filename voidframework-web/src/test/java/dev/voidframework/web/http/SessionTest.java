package dev.voidframework.web.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class SessionTest {

    @Test
    void equalsBothEqual() {

        // Arrange
        final Session sessionOne = new Session();
        sessionOne.put("key", "value");

        final Session sessionTwo = new Session();
        sessionTwo.put("key", "value");

        // Act
        final boolean isEqual = sessionOne.equals(sessionTwo);

        // Assert
         Assertions.assertTrue(isEqual);
    }

    @Test
    void equalsNotEqual() {

        // Arrange
        final Session sessionOne = new Session();
        sessionOne.put("key", "value");

        final Session sessionTwo = new Session();
        sessionTwo.put("key", "value2");

        // Act
        final boolean isEqual = sessionOne.equals(sessionTwo);

        // Assert
        Assertions.assertFalse(isEqual);
    }
}
