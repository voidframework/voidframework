package dev.voidframework.core.helper;

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
public final class ReflectionTest {

    @Test
    public void getAnnotatedFieldFromValue() throws IllegalAccessException {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuid);

        // Act
        final Field field = Reflection.getAnnotatedField(demo, Validate.class);

        // Assert
        Assertions.assertNotNull(field);
        Assertions.assertEquals("id", field.getName());
        Assertions.assertEquals(uuid, field.get(demo));
    }

    @Test
    public void getAnnotatedFieldFromClassType() {

        // Act
        final Field field = Reflection.getAnnotatedField(Demo.class, Validate.class);

        // Assert
        Assertions.assertNotNull(field);
        Assertions.assertEquals("id", field.getName());
    }

    @Test
    public void getAnnotatedFieldFromClassTypeWithInheritance() {

        // Act
        final Field field = Reflection.getAnnotatedField(DemoChild.class, Validate.class);

        // Assert
        Assertions.assertNotNull(field);
        Assertions.assertEquals("id", field.getName());
    }

    @Test
    public void getAnnotatedFieldWithNullValue() {

        // Act
        final Field field = Reflection.getAnnotatedField((Demo) null, Validate.class);

        // Assert
        Assertions.assertNull(field);
    }

    @Test
    public void getAnnotatedFieldWithNullClassType() {

        // Act
        final Field field = Reflection.getAnnotatedField((Class<?>) null, Validate.class);

        // Assert
        Assertions.assertNull(field);
    }

    @Test
    public void getFieldValueExplicitClassType() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuid);

        // Act
        final UUID value = Reflection.getFieldValue(demo, "id", UUID.class);

        // Assert
        Assertions.assertNotNull(value);
        Assertions.assertEquals(uuid, value);
    }

    @Test
    public void getFieldValueExplicitClassTypeUnknownField() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuid);
        demo.setFirstName("Marie");

        // Act
        final UUID value = Reflection.getFieldValue(demo, "unknownField", UUID.class);

        // Assert
        Assertions.assertNull(value);
    }

    @Test
    public void getFieldValueWrappedClassType() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuid);

        // Act
        final UUID value = Reflection.getFieldValue(demo, "id", new Reflection.WrappedClass<>());

        // Assert
        Assertions.assertNotNull(value);
        Assertions.assertEquals(uuid, value);
    }

    @Test
    public void getFieldValueWrappedClassTypeUnknownField() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuid);
        demo.setFirstName("Bob");

        // Act
        final UUID value = Reflection.getFieldValue(demo, "unknownField", new Reflection.WrappedClass<>());

        // Assert
        Assertions.assertNull(value);
    }

    @Test
    public void setFieldValue() {

        // Arrange
        final UUID uuidCurrent = UUID.randomUUID();
        final UUID uuidNew = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuidCurrent);

        // Act
        Reflection.setFieldValue(demo, "id", uuidNew);

        // Assert
        Assertions.assertNotEquals(uuidCurrent, uuidNew);
        Assertions.assertEquals(demo.id, uuidNew);
    }

    @Test
    public void setFieldValueUnknownField() {

        // Arrange
        final UUID uuidCurrent = UUID.randomUUID();
        final UUID uuidNew = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuidCurrent);
        demo.setFirstName("Bob");

        // Act
        Reflection.setFieldValue(demo, "unknownField", uuidNew);

        // Assert
        Assertions.assertEquals(demo.id, uuidCurrent);
        Assertions.assertEquals(demo.firstName, "Bob");
        Assertions.assertNotEquals(uuidCurrent, uuidNew);
    }

    @Test
    public void resolveMethod() throws InvocationTargetException, IllegalAccessException {

        // Arrange
        final UUID uuidCurrent = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuidCurrent);

        // Act
        final Method method = Reflection.resolveMethod("getId", Demo.class);

        // Assert
        Assertions.assertNotNull(method);
        Assertions.assertEquals("getId", method.getName());
        Assertions.assertEquals(uuidCurrent, method.invoke(demo));
    }

    @Test
    public void resolveMethodNotFound() {

        // Act
        final Method method = Reflection.resolveMethod("unknownMethod", Demo.class);

        // Assert
        Assertions.assertNull(method);
    }

    @Test
    public void callMethodReturnSomething() {

        // Arrange
        final UUID uuidCurrent = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuidCurrent);

        // Act
        final UUID uuid = Reflection.callMethod(demo, "getId", UUID.class, new Class[]{});

        // Assert
        Assertions.assertNotNull(uuid);
        Assertions.assertEquals(uuidCurrent, uuid);
    }

    @Test
    public void callMethodReturnSomethingUnknownMethod() {

        // Arrange
        final UUID uuidCurrent = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuidCurrent);

        // Act
        final UUID uuid = Reflection.callMethod(demo, "unknownMethod", UUID.class, new Class[]{});

        // Assert
        Assertions.assertNull(uuid);
    }

    @Test
    public void callMethodReturnNothing() {

        // Arrange
        final Demo demo = new Demo();

        // Act
        Reflection.callMethod(demo, "setFirstName", new Class[]{String.class}, "Vanessa");

        // Assert
        Assertions.assertEquals("Vanessa", demo.getFirstName());
    }

    @Test
    public void callMethodReturnNothingUnknownMethod() {

        // Arrange
        final Demo demo = new Demo();
        demo.setId(UUID.fromString("bc35ec9f-9a74-4843-8504-e391e3d2fd15"));
        demo.setFirstName("Mina");

        // Act
        Reflection.callMethod(demo, "unknownMethod", new Class[]{String.class}, "Vanessa");

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

        public void setFirstName(final String firstName) {

            this.firstName = firstName;
        }

        public UUID getId() {

            return id;
        }

        public void setId(final UUID id) {

            this.id = id;
        }
    }
}
