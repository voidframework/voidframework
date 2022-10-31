package dev.voidframework.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Validate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class ReflectionUtilsTest {

    @Test
    void getAnnotatedFieldFromValue() throws IllegalAccessException {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuid);

        // Act
        final Field field = ReflectionUtils.getAnnotatedField(demo, Validate.class);

        // Assert
        Assertions.assertNotNull(field);
        Assertions.assertEquals("id", field.getName());
        Assertions.assertEquals(uuid, field.get(demo));
    }

    @Test
    void getAnnotatedFieldFromClassType() {

        // Act
        final Field field = ReflectionUtils.getAnnotatedField(Demo.class, Validate.class);

        // Assert
        Assertions.assertNotNull(field);
        Assertions.assertEquals("id", field.getName());
    }

    @Test
    void getAnnotatedFieldFromClassTypeWithInheritance() {

        // Act
        final Field field = ReflectionUtils.getAnnotatedField(DemoChild.class, Validate.class);

        // Assert
        Assertions.assertNotNull(field);
        Assertions.assertEquals("id", field.getName());
    }

    @Test
    void getAnnotatedFieldWithNullValue() {

        // Act
        final Field field = ReflectionUtils.getAnnotatedField((Demo) null, Validate.class);

        // Assert
        Assertions.assertNull(field);
    }

    @Test
    void getAnnotatedFieldWithNullClassType() {

        // Act
        final Field field = ReflectionUtils.getAnnotatedField(null, Validate.class);

        // Assert
        Assertions.assertNull(field);
    }

    @Test
    void getFieldValueExplicitClassType() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuid);

        // Act
        final UUID value = ReflectionUtils.getFieldValue(demo, "id", UUID.class);

        // Assert
        Assertions.assertNotNull(value);
        Assertions.assertEquals(uuid, value);
    }

    @Test
    void getFieldValueExplicitClassTypeUnknownField() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuid);
        demo.setFirstName("Marie");

        // Act
        final UUID value = ReflectionUtils.getFieldValue(demo, "unknownField", UUID.class);

        // Assert
        Assertions.assertNull(value);
    }

    @Test
    void getFieldValueWrappedClassType() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuid);

        // Act
        final UUID value = ReflectionUtils.getFieldValue(demo, "id", new ReflectionUtils.WrappedClass<>());

        // Assert
        Assertions.assertNotNull(value);
        Assertions.assertEquals(uuid, value);
    }

    @Test
    void getFieldValueWrappedClassTypeUnknownField() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuid);
        demo.setFirstName("Bob");

        // Act
        final UUID value = ReflectionUtils.getFieldValue(demo, "unknownField", new ReflectionUtils.WrappedClass<>());

        // Assert
        Assertions.assertNull(value);
    }

    @Test
    void setFieldValue() {

        // Arrange
        final UUID uuidCurrent = UUID.randomUUID();
        final UUID uuidNew = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuidCurrent);

        // Act
        ReflectionUtils.setFieldValue(demo, "id", uuidNew);

        // Assert
        Assertions.assertNotEquals(uuidCurrent, uuidNew);
        Assertions.assertEquals(demo.id, uuidNew);
    }

    @Test
    void setFieldValueUnknownField() {

        // Arrange
        final UUID uuidCurrent = UUID.randomUUID();
        final UUID uuidNew = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuidCurrent);
        demo.setFirstName("Bob");

        // Act
        ReflectionUtils.setFieldValue(demo, "unknownField", uuidNew);

        // Assert
        Assertions.assertEquals(demo.id, uuidCurrent);
        Assertions.assertEquals("Bob", demo.firstName);
        Assertions.assertNotEquals(uuidCurrent, uuidNew);
    }

    @Test
    void resolveMethod() throws InvocationTargetException, IllegalAccessException {

        // Arrange
        final UUID uuidCurrent = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuidCurrent);

        // Act
        final Method method = ReflectionUtils.resolveMethod("getId", Demo.class);

        // Assert
        Assertions.assertNotNull(method);
        Assertions.assertEquals("getId", method.getName());
        Assertions.assertEquals(uuidCurrent, method.invoke(demo));
    }

    @Test
    void resolveMethodNotFound() {

        // Act
        final Method method = ReflectionUtils.resolveMethod("unknownMethod", Demo.class);

        // Assert
        Assertions.assertNull(method);
    }

    @Test
    void callMethodReturnSomething() {

        // Arrange
        final UUID uuidCurrent = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuidCurrent);

        // Act
        final UUID uuid = ReflectionUtils.callMethod(demo, "getId", UUID.class, new Class[]{});

        // Assert
        Assertions.assertNotNull(uuid);
        Assertions.assertEquals(uuidCurrent, uuid);
    }

    @Test
    void callMethodReturnSomethingUnknownMethod() {

        // Arrange
        final UUID uuidCurrent = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuidCurrent);

        // Act
        final UUID uuid = ReflectionUtils.callMethod(demo, "unknownMethod", UUID.class, new Class[]{});

        // Assert
        Assertions.assertNull(uuid);
    }

    @Test
    void callMethodReturnNothing() {

        // Arrange
        final Demo demo = new Demo();

        // Act
        ReflectionUtils.callMethod(demo, "setFirstName", new Class[]{String.class}, "Vanessa");

        // Assert
        Assertions.assertEquals("Vanessa", demo.getFirstName());
    }

    @Test
    void callMethodReturnNothingUnknownMethod() {

        // Arrange
        final Demo demo = new Demo();
        demo.setId(UUID.fromString("bc35ec9f-9a74-4843-8504-e391e3d2fd15"));
        demo.setFirstName("Mina");

        // Act
        ReflectionUtils.callMethod(demo, "unknownMethod", new Class[]{String.class}, "Vanessa");

        // Assert
        Assertions.assertEquals(UUID.fromString("bc35ec9f-9a74-4843-8504-e391e3d2fd15"), demo.getId());
        Assertions.assertEquals("Mina", demo.getFirstName());
    }

    /**
     * Demo child class. To test inheritance.
     */
    public static class DemoChild extends Demo {
    }

    /**
     * Demo class.
     */
    public static class Demo {

        private String firstName;

        @Validate
        private UUID id;

        public String getFirstName() {

            return firstName;
        }

        void setFirstName(final String firstName) {

            this.firstName = firstName;
        }

        public UUID getId() {

            return id;
        }

        void setId(final UUID id) {

            this.id = id;
        }
    }
}
