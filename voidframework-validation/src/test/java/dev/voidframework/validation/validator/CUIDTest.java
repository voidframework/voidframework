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
final class CUIDTest {

    @Test
    void cuidAsStringWithError() {

        // Arrange
        final PojoAsString pojo = new PojoAsString(" qklsjd ");
        final Validation validation = new Validation();

        // Act
        final Validated<PojoAsString> pojoValidated = validation.validate(pojo, Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertTrue(pojoValidated.hasError());
        Assertions.assertFalse(pojoValidated.isValid());

        final ValidationError validationError = pojoValidated.getError("cuid");
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertEquals("voidframework.validation.constraints.CUID.message", validationError.messageKey());
        Assertions.assertEquals(0, validationError.argumentArray().length);

        Assertions.assertEquals(pojo, pojoValidated.getInstance());
    }

    @Test
    void cuidAsStringWithoutError() {

        // Arrange
        final PojoAsString pojo = new PojoAsString("cjld2cyuq0000t3rmniod1foy");
        final Validation validation = new Validation();

        // Act
        final Validated<PojoAsString> pojoValidated = validation.validate(pojo, Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertFalse(pojoValidated.hasError());
        Assertions.assertTrue(pojoValidated.isValid());

        Assertions.assertEquals(pojo, pojoValidated.getInstance());
    }

    @Test
    void cuidAsByteArrayWithError() {

        // Arrange
        final PojoAsByteArray pojo = new PojoAsByteArray(new byte[]{'a'});
        final Validation validation = new Validation();

        // Act
        final Validated<PojoAsByteArray> pojoValidated = validation.validate(pojo, Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertTrue(pojoValidated.hasError());
        Assertions.assertFalse(pojoValidated.isValid());

        final ValidationError validationError = pojoValidated.getError("cuid");
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertEquals("voidframework.validation.constraints.CUID.message", validationError.messageKey());
        Assertions.assertEquals(0, validationError.argumentArray().length);

        Assertions.assertEquals(pojo, pojoValidated.getInstance());
    }

    @Test
    void cuidAsByteArrayWithoutError() {

        // Arrange
        final PojoAsByteArray pojo = new PojoAsByteArray(new byte[]{
            'c', 'j', 'l', 'd', '2', 'c', 'y', 'u', 'q', '0', '0', '0', '0', 't', '3', 'r', 'm', 'n', 'i', 'o', 'd', '1', 'f', 'o', 'y',});
        final Validation validation = new Validation();

        // Act
        final Validated<PojoAsByteArray> pojoValidated = validation.validate(pojo, Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(pojoValidated);
        Assertions.assertFalse(pojoValidated.hasError());
        Assertions.assertTrue(pojoValidated.isValid());

        Assertions.assertEquals(pojo, pojoValidated.getInstance());
    }

    /**
     * Pojo with CUID as String.
     *
     * @param cuid CUID
     */
    private record PojoAsString(@CUID String cuid) {
    }

    /**
     * Pojo with CUID as byte array.
     *
     * @param cuid CUID
     */
    private record PojoAsByteArray(@CUID byte[] cuid) {
    }
}
