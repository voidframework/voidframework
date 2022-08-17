package dev.voidframework.validation.validator;

import dev.voidframework.validation.Validated;
import dev.voidframework.validation.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class InputStreamSizeTest {

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
