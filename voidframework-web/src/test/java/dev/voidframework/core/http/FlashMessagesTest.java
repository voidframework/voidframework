package dev.voidframework.core.http;

import dev.voidframework.web.http.FlashMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class FlashMessagesTest {

    @Test
    void equalsBothEqual() {

        // Arrange
        final FlashMessages flashMessagesOne = new FlashMessages();
        flashMessagesOne.put("key", "value");

        final FlashMessages flashMessagesTwo = new FlashMessages();
        flashMessagesTwo.put("key", "value");

        // Act
        final boolean isEqual = flashMessagesOne.equals(flashMessagesTwo);

        // Assert
        Assertions.assertTrue(isEqual);
    }

    @Test
    void equalsNotEqual() {

        // Arrange
        final FlashMessages flashMessagesOne = new FlashMessages();
        flashMessagesOne.put("key", "value");

        final FlashMessages flashMessagesTwo = new FlashMessages();
        flashMessagesTwo.put("key", "value2");

        // Act
        final boolean isEqual = flashMessagesOne.equals(flashMessagesTwo);

        // Assert
        Assertions.assertFalse(isEqual);
    }
}
