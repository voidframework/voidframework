package dev.voidframework.validation.validator;

import dev.voidframework.validation.Validated;
import dev.voidframework.validation.Validation;
import dev.voidframework.validation.ValidationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Locale;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class InstanceTest {

    @Test
    void withError() {

        // Arrange
        final Pojo pojo = new Pojo(1337);
        final Validation validation = new Validation();

        // Act
        final Validated<Pojo> pojoValidated = validation.validate(pojo, Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertTrue(pojoValidated.hasError());
        Assertions.assertFalse(pojoValidated.isValid());

        final ValidationError validationError = pojoValidated.getError("object");
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertEquals("voidframework.validation.constraints.Instance.message", validationError.messageKey());
        Assertions.assertEquals(1, validationError.argumentArray().length);
        Assertions.assertArrayEquals(new Class<?>[]{String.class, List.class}, (Class<?>[]) validationError.argumentArray()[0]);

        Assertions.assertEquals(pojo, pojoValidated.getInstance());
    }

    @Test
    void withoutErrorString() {

        // Arrange
        final Pojo pojo = new Pojo("string");
        final Validation validation = new Validation();

        // Act
        final Validated<Pojo> pojoValidated = validation.validate(pojo, Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertFalse(pojoValidated.hasError());
        Assertions.assertTrue(pojoValidated.isValid());

        Assertions.assertEquals(pojo, pojoValidated.getInstance());
    }

    @Test
    void withoutErrorList() {

        // Arrange
        final Pojo pojo = new Pojo(List.of(42));
        final Validation validation = new Validation();

        // Act
        final Validated<Pojo> pojoValidated = validation.validate(pojo, Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertFalse(pojoValidated.hasError());
        Assertions.assertTrue(pojoValidated.isValid());

        Assertions.assertEquals(pojo, pojoValidated.getInstance());
    }

    /**
     * Pojo.
     *
     * @param object Object instance
     */
    private record Pojo(@Instance({String.class, List.class}) Object object) {
    }
}
