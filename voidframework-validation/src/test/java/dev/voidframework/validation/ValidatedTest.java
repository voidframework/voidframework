package dev.voidframework.validation;

import dev.voidframework.core.helper.Reflection;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class ValidatedTest {

    @Test
    void emptyOf() {

        // Act
        final Validated<Object> validated = Validated.emptyOf();

        // Asset
        Assertions.assertNotNull(validated);

        final Object objInstance = Reflection.getFieldValue(validated, "instance", Object.class);
        final Map<String, List<ValidationError>> validationErrorPerPathMap = Reflection.getFieldValue(
            validated,
            "validationErrorPerPathMap",
            new Reflection.WrappedClass<>());

        Assertions.assertNull(objInstance);
        Assertions.assertNotNull(validationErrorPerPathMap);
        Assertions.assertTrue(validationErrorPerPathMap.isEmpty());
    }

    @Test
    void emptyOfWithEntity() {

        // Arrange
        final SimpleEntity entity = new SimpleEntity("a@a");

        // Act
        final Validated<Object> validated = Validated.emptyOf(entity);

        // Asset
        Assertions.assertNotNull(validated);

        final Object objInstance = Reflection.getFieldValue(validated, "instance", Object.class);
        Assertions.assertNotNull(objInstance);
        Assertions.assertTrue(objInstance instanceof SimpleEntity);

        final String email = Reflection.getFieldValue(objInstance, "email", String.class);
        Assertions.assertEquals("a@a", email);

        final Map<String, List<ValidationError>> validationErrorPerPathMap = Reflection.getFieldValue(
            validated,
            "validationErrorPerPathMap",
            new Reflection.WrappedClass<>());
        Assertions.assertNotNull(validationErrorPerPathMap);
        Assertions.assertTrue(validationErrorPerPathMap.isEmpty());
    }

    @Test
    void hasError() {

        // Arrange
        final List<ValidationError> validationErrorList = List.of(new ValidationError("message", "messageKey"));
        final Map<String, List<ValidationError>> validationErrorPerPathMap = Map.of("fieldName", validationErrorList);
        final Validated<Object> validated = new Validated<>(null, validationErrorPerPathMap);

        // Act
        final boolean hasError = validated.hasError();

        // Asset
        Assertions.assertTrue(hasError);
    }

    @Test
    void hasErrorNoErrors() {

        // Arrange
        final Map<String, List<ValidationError>> validationErrorPerPathMap = Map.of();
        final Validated<Object> validated = new Validated<>(null, validationErrorPerPathMap);

        // Act
        final boolean hasError = validated.hasError();

        // Asset
        Assertions.assertFalse(hasError);
    }

    @Test
    void hasErrorSpecificPath() {

        // Arrange
        final List<ValidationError> validationErrorList = List.of(new ValidationError("message", "messageKey"));
        final Map<String, List<ValidationError>> validationErrorPerPathMap = Map.of("fieldName", validationErrorList);
        final Validated<Object> validated = new Validated<>(null, validationErrorPerPathMap);

        // Act
        final boolean hasError = validated.hasError("fieldName");

        // Asset
        Assertions.assertTrue(hasError);
    }

    @Test
    void hasErrorSpecificPathNoErrors() {

        // Arrange
        final List<ValidationError> validationErrorList = List.of(new ValidationError("message", "messageKey"));
        final Map<String, List<ValidationError>> validationErrorPerPathMap = Map.of("fieldName", validationErrorList);
        final Validated<Object> validated = new Validated<>(null, validationErrorPerPathMap);

        // Act
        final boolean hasError = validated.hasError("fieldName2");

        // Asset
        Assertions.assertFalse(hasError);
    }

    @Test
    void isValid() {

        // Arrange
        final Map<String, List<ValidationError>> validationErrorPerPathMap = Map.of();
        final Validated<Object> validated = new Validated<>(null, validationErrorPerPathMap);

        // Act
        final boolean isValid = validated.isValid();

        // Asset
        Assertions.assertTrue(isValid);
    }

    @Test
    void isValidNotValid() {

        // Arrange
        final List<ValidationError> validationErrorList = List.of(new ValidationError("message", "messageKey"));
        final Map<String, List<ValidationError>> validationErrorPerPathMap = Map.of("fieldName", validationErrorList);
        final Validated<Object> validated = new Validated<>(null, validationErrorPerPathMap);

        // Act
        final boolean isValid = validated.isValid();

        // Asset
        Assertions.assertFalse(isValid);
    }

    @Test
    void getInstance() {

        // Arrange
        final SimpleEntity entity = new SimpleEntity("name@domain.local");
        final Map<String, List<ValidationError>> validationErrorPerPathMap = Map.of();
        final Validated<SimpleEntity> validated = new Validated<>(entity, validationErrorPerPathMap);

        // Act
        final SimpleEntity entityInstance = validated.getInstance();

        // Asset
        Assertions.assertNotNull(entityInstance);
        Assertions.assertEquals("name@domain.local", entityInstance.email);
    }

    @Test
    void getError() {

        // Arrange
        final List<ValidationError> validationErrorList = List.of(new ValidationError("message", "messageKey"));
        final Map<String, List<ValidationError>> validationErrorPerPathMap = Map.of("fieldName", validationErrorList);
        final Validated<Object> validated = new Validated<>(null, validationErrorPerPathMap);

        // Act
        final Map<String, List<ValidationError>> validationErrorMap = validated.getError();

        // Asset
        Assertions.assertNotNull(validationErrorMap);
        Assertions.assertEquals(1, validationErrorMap.size());
        Assertions.assertTrue(validationErrorMap.containsKey("fieldName"));

        final List<ValidationError> validationErrorListActual = validationErrorMap.get("fieldName");
        Assertions.assertEquals(1, validationErrorListActual.size());
        Assertions.assertEquals("message", validationErrorListActual.get(0).getMessage());
        Assertions.assertEquals("messageKey", validationErrorListActual.get(0).getMessageKey());
    }

    @Test
    void getErrorSpecificPath() {

        // Arrange
        final List<ValidationError> validationErrorList = List.of(new ValidationError("message", "messageKey"));
        final Map<String, List<ValidationError>> validationErrorPerPathMap = Map.of("fieldName", validationErrorList);
        final Validated<Object> validated = new Validated<>(null, validationErrorPerPathMap);

        // Act
        final ValidationError validationError = validated.getError("fieldName");

        // Asset
        Assertions.assertNotNull(validationError);
        Assertions.assertEquals("message", validationError.getMessage());
        Assertions.assertEquals("messageKey", validationError.getMessageKey());
    }

    @Test
    void getErrorList() {

        // Arrange
        final List<ValidationError> validationErrorList = List.of(new ValidationError("message", "messageKey"));
        final Map<String, List<ValidationError>> validationErrorPerPathMap = Map.of("fieldName", validationErrorList);
        final Validated<Object> validated = new Validated<>(null, validationErrorPerPathMap);

        // Act
        final List<ValidationError> validationErrorListActual = validated.getErrorList("fieldName");

        // Asset
        Assertions.assertNotNull(validationErrorListActual);
        Assertions.assertEquals(1, validationErrorListActual.size());
        Assertions.assertEquals("message", validationErrorListActual.get(0).getMessage());
        Assertions.assertEquals("messageKey", validationErrorListActual.get(0).getMessageKey());
    }

    @Test
    void getErrorListNotFound() {

        // Arrange
        final List<ValidationError> validationErrorList = List.of(new ValidationError("message", "messageKey"));
        final Map<String, List<ValidationError>> validationErrorPerPathMap = Map.of("fieldName", validationErrorList);
        final Validated<Object> validated = new Validated<>(null, validationErrorPerPathMap);

        // Act
        final List<ValidationError> validationErrorListActual = validated.getErrorList("fieldName2");

        // Asset
        Assertions.assertNotNull(validationErrorListActual);
        Assertions.assertEquals(0, validationErrorListActual.size());
    }

    /**
     * Simple entity.
     *
     * @param email The email address
     */
    private record SimpleEntity(@Email @Length(min = 5, max = 10) String email) {
    }
}
