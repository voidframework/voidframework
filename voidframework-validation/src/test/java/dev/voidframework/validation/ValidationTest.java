package dev.voidframework.validation;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Locale;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class ValidationTest {

    @BeforeAll
    public static void beforeAll() {

        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    public void validateNullValue() {

        // Arrange
        final Validation validation = new Validation();

        // Act
        final Validated<SimpleEntity> entityValidated = validation.validate(null, Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(entityValidated);
        Assertions.assertFalse(entityValidated.hasError());
        Assertions.assertTrue(entityValidated.isValid());
    }

    @Test
    public void validateNullValueWithoutLocale() {

        // Arrange
        final Validation validation = new Validation();

        // Act
        final Validated<SimpleEntity> entityValidated = validation.validate(null);

        // Assert
        Assertions.assertNotNull(entityValidated);
        Assertions.assertFalse(entityValidated.hasError());
        Assertions.assertTrue(entityValidated.isValid());
    }

    @Test
    public void validateWithError() {

        // Arrange
        final SimpleEntityWithConstraintGroups entity = new SimpleEntityWithConstraintGroups("Camille", "aa");
        final Validation validation = new Validation();

        // Act
        final Validated<SimpleEntityWithConstraintGroups> entityValidated = validation.validate(
            entity,
            Locale.ENGLISH,
            GroupOne.class,
            GroupTwo.class);

        // Assert
        Assertions.assertNotNull(entityValidated);
        Assertions.assertTrue(entityValidated.hasError());
        Assertions.assertFalse(entityValidated.isValid());

        final List<ValidationError> validationErrorList = entityValidated.getErrorList("email");
        Assertions.assertNotNull(validationErrorList);
        Assertions.assertEquals(2, validationErrorList.size());

        final int idxLength = validationErrorList.get(0).getMessageKey().contains("Length.message") ? 0 : 1;
        Assertions.assertEquals("length must be between 5 and 125", validationErrorList.get(idxLength).getMessage());
        Assertions.assertEquals("org.hibernate.validator.constraints.Length.message", validationErrorList.get(idxLength).getMessageKey());
        Assertions.assertEquals(2, validationErrorList.get(idxLength).getArgumentArray().length);
        Assertions.assertEquals(5, validationErrorList.get(idxLength).getArgumentArray()[0]);
        Assertions.assertEquals(125, validationErrorList.get(idxLength).getArgumentArray()[1]);

        final int idxEmail = validationErrorList.get(0).getMessageKey().contains("Email.message") ? 0 : 1;
        Assertions.assertEquals("must be a well-formed email address", validationErrorList.get(idxEmail).getMessage());
        Assertions.assertEquals("jakarta.validation.constraints.Email.message", validationErrorList.get(idxEmail).getMessageKey());
        Assertions.assertEquals(0, validationErrorList.get(idxEmail).getArgumentArray().length);

        Assertions.assertEquals(entity, entityValidated.getInstance());
    }

    @Test
    public void validateWithErrorWithoutLocale() {

        // Arrange
        final SimpleEntityWithConstraintGroups entity = new SimpleEntityWithConstraintGroups("Camille", "aa");
        final Validation validation = new Validation();

        // Act
        final Validated<SimpleEntityWithConstraintGroups> entityValidated = validation.validate(
            entity,
            Locale.ENGLISH,
            GroupOne.class,
            GroupTwo.class);

        // Assert
        Assertions.assertNotNull(entityValidated);
        Assertions.assertTrue(entityValidated.hasError());
        Assertions.assertFalse(entityValidated.isValid());

        final List<ValidationError> validationErrorList = entityValidated.getErrorList("email");
        Assertions.assertNotNull(validationErrorList);
        Assertions.assertEquals(2, validationErrorList.size());

        final int idxLength = validationErrorList.get(0).getMessageKey().contains("Length.message") ? 0 : 1;
        Assertions.assertEquals("length must be between 5 and 125", validationErrorList.get(idxLength).getMessage());
        Assertions.assertEquals("org.hibernate.validator.constraints.Length.message", validationErrorList.get(idxLength).getMessageKey());
        Assertions.assertEquals(2, validationErrorList.get(idxLength).getArgumentArray().length);
        Assertions.assertEquals(5, validationErrorList.get(idxLength).getArgumentArray()[0]);
        Assertions.assertEquals(125, validationErrorList.get(idxLength).getArgumentArray()[1]);

        final int idxEmail = validationErrorList.get(0).getMessageKey().contains("Email.message") ? 0 : 1;
        Assertions.assertEquals("must be a well-formed email address", validationErrorList.get(idxEmail).getMessage());
        Assertions.assertEquals("jakarta.validation.constraints.Email.message", validationErrorList.get(idxEmail).getMessageKey());
        Assertions.assertEquals(0, validationErrorList.get(idxEmail).getArgumentArray().length);

        Assertions.assertEquals(entity, entityValidated.getInstance());
    }

    @Test
    public void validateWithoutError() {

        // Arrange
        final SimpleEntity simpleEntity = new SimpleEntity("abc@local");
        final Validation validation = new Validation();

        // Act
        final Validated<SimpleEntity> entityValidated = validation.validate(simpleEntity, Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(entityValidated);
        Assertions.assertFalse(entityValidated.hasError());
        Assertions.assertTrue(entityValidated.isValid());

        Assertions.assertEquals(simpleEntity, entityValidated.getInstance());
    }

    @Test
    public void validateWithconstraintGroupWithGroupOne() {

        // Arrange
        final SimpleEntityWithConstraintGroups entity = new SimpleEntityWithConstraintGroups("Bob", "");
        final Validation validation = new Validation();

        // Act
        final Validated<SimpleEntityWithConstraintGroups> entityValidated = validation.validate(entity, Locale.ENGLISH, GroupOne.class);

        // Assert
        Assertions.assertNotNull(entityValidated);
        Assertions.assertFalse(entityValidated.hasError());
        Assertions.assertTrue(entityValidated.isValid());

        Assertions.assertEquals(entity, entityValidated.getInstance());
    }

    @Test
    public void validateWithconstraintGroupWithGroupTwo() {

        // Arrange
        final SimpleEntityWithConstraintGroups entity = new SimpleEntityWithConstraintGroups("", "abc@domain.local");
        final Validation validation = new Validation();

        // Act
        final Validated<SimpleEntityWithConstraintGroups> entityValidated = validation.validate(entity, Locale.ENGLISH, GroupTwo.class);

        // Assert
        Assertions.assertNotNull(entityValidated);
        Assertions.assertFalse(entityValidated.hasError());
        Assertions.assertTrue(entityValidated.isValid());

        Assertions.assertEquals(entity, entityValidated.getInstance());
    }

    /**
     * Constraint group 1.
     */
    private interface GroupOne {
    }

    /**
     * Constraint group 1.
     */
    private interface GroupTwo {
    }

    /**
     * Simple entity.
     *
     * @param firstName The first name
     */
    private record SimpleEntity(@Length(min = 1, max = 10) String firstName) {
    }

    /**
     * Simple entity with constraint groups.
     *
     * @param firstName The first name
     * @param email     The email address
     */
    private record SimpleEntityWithConstraintGroups(@Length(min = 1, max = 10, groups = GroupOne.class) String firstName,
                                                    @Email(groups = GroupTwo.class) @Length(min = 5, max = 125, groups = GroupTwo.class) String email) {
    }
}
