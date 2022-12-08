package dev.voidframework.validation.validator;

import dev.voidframework.validation.Validated;
import dev.voidframework.validation.Validation;
import dev.voidframework.validation.ValidationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class InputStreamSizeTest {

    @Test
    void withError() {

        // Arrange
        final Pojo pojo = new Pojo(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
        final Validation validation = new Validation();

        // Act
        final Validated<Pojo> pojoValidated = validation.validate(pojo, Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertTrue(pojoValidated.hasError());
        Assertions.assertFalse(pojoValidated.isValid());

        final ValidationError validationError = pojoValidated.getError("is");
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertEquals("jakarta.validation.constraints.Size.message", validationError.messageKey());
        Assertions.assertEquals(2, validationError.argumentArray().length);
        Assertions.assertEquals(1L, validationError.argumentArray()[0]);
        Assertions.assertEquals(10L, validationError.argumentArray()[1]);

        Assertions.assertEquals(pojo, pojoValidated.getInstance());
    }

    @Test
    void withoutError() {

        // Arrange
        final Pojo pojo = new Pojo(new ByteArrayInputStream("123456".getBytes(StandardCharsets.UTF_8)));
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
     * @param is The input stream
     */
    private record Pojo(@InputStreamSize(min = 1, max = 10) InputStream is) {
    }
}
