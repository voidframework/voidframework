package dev.voidframework.validation.validator;

import dev.voidframework.validation.Validated;
import dev.voidframework.validation.Validation;
import dev.voidframework.validation.ValidationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Locale;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class TrimmedSizeTest {

    @Test
    void withError() {

        // Arrange
        final Pojo pojo = new Pojo("                                 ");
        final Validation validation = new Validation();

        // Act
        final Validated<Pojo> pojoValidated = validation.validate(pojo, Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertTrue(pojoValidated.hasError());
        Assertions.assertFalse(pojoValidated.isValid());

        final ValidationError validationError = pojoValidated.getError("firstName");
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertEquals("org.hibernate.validator.constraints.Length.message", validationError.messageKey());
        Assertions.assertEquals(2, validationError.argumentArray().length);
        Assertions.assertEquals(1, validationError.argumentArray()[0]);
        Assertions.assertEquals(10, validationError.argumentArray()[1]);

        Assertions.assertEquals(pojo, pojoValidated.getInstance());
    }

    @Test
    void withoutError() {

        // Arrange
        final Pojo pojo = new Pojo("           abc@local             ");
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
     * @param firstName The first name
     */
    private record Pojo(@TrimmedLength(min = 1, max = 10) String firstName) {
    }
}
