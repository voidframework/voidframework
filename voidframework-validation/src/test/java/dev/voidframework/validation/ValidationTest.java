package dev.voidframework.validation;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Locale;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class ValidationTest {

    @Test
    public void withError() {

        // Arrange
        final Pojo pojo = new Pojo("Camille Dominique");
        final Validation validation = new Validation();

        // Act
        final Validated<Pojo> pojoValidated = validation.validate(pojo, Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertTrue(pojoValidated.hasError());
        Assertions.assertFalse(pojoValidated.isValid());

        final List<ValidationError> validationErrorList = pojoValidated.getErrorList("firstName");
        Assertions.assertNotNull(validationErrorList);
        Assertions.assertEquals(2, validationErrorList.size());

        final int idxLength = validationErrorList.get(0).getMessageKey().contains("Length.message") ? 0 : 1;
        Assertions.assertEquals("length must be between 1 and 10", validationErrorList.get(idxLength).getMessage());
        Assertions.assertEquals("org.hibernate.validator.constraints.Length.message", validationErrorList.get(idxLength).getMessageKey());
        Assertions.assertEquals(2, validationErrorList.get(idxLength).getArgumentArray().length);
        Assertions.assertEquals(1, validationErrorList.get(idxLength).getArgumentArray()[0]);
        Assertions.assertEquals(10, validationErrorList.get(idxLength).getArgumentArray()[1]);

        final int idxEmail = validationErrorList.get(0).getMessageKey().contains("Email.message") ? 0 : 1;
        Assertions.assertEquals("must be a well-formed email address", validationErrorList.get(idxEmail).getMessage());
        Assertions.assertEquals("jakarta.validation.constraints.Email.message", validationErrorList.get(idxEmail).getMessageKey());
        Assertions.assertEquals(0, validationErrorList.get(idxEmail).getArgumentArray().length);

        Assertions.assertEquals(pojo, pojoValidated.getInstance());
    }

    @Test
    public void withoutError() {

        // Arrange
        final Pojo pojo = new Pojo("abc@local");
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
    private record Pojo(@Email @Length(min = 1, max = 10) String firstName) {
    }
}
