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
    public void getAnnotatedField() throws IllegalAccessException {

        final UUID uuid = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuid);

        final Field field = Reflection.getAnnotatedField(demo, Validate.class);
        Assertions.assertNotNull(field);

        Assertions.assertEquals("id", field.getName());
        Assertions.assertEquals(uuid, field.get(demo));
    }

    @Test
    public void getAnnotatedFieldWithNullValue() {

        final Field field = Reflection.getAnnotatedField(null, Validate.class);
        Assertions.assertNull(field);
    }

    @Test
    public void getFieldValueExplicitClassType() {

        final UUID uuid = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuid);

        final UUID value = Reflection.getFieldValue(demo, "id", UUID.class);

        Assertions.assertNotNull(value);
        Assertions.assertEquals(uuid, value);
    }

    @Test
    public void getFieldValueWrappedClassType() {

        final UUID uuid = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuid);

        final UUID value = Reflection.getFieldValue(demo, "id", new Reflection.WrappedClass<>());

        Assertions.assertNotNull(value);
        Assertions.assertEquals(uuid, value);
    }

    @Test
    public void setFieldValue() {

        final UUID uuidCurrent = UUID.randomUUID();
        final UUID uuidNew = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuidCurrent);

        Reflection.setFieldValue(demo, "id", uuidNew);

        Assertions.assertNotEquals(uuidCurrent, uuidNew);
        Assertions.assertEquals(demo.id, uuidNew);
    }

    @Test
    public void resolveMethod() throws InvocationTargetException, IllegalAccessException {

        final UUID uuidCurrent = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuidCurrent);

        final Method method = Reflection.resolveMethod("getId", Demo.class);
        Assertions.assertNotNull(method);
        Assertions.assertEquals("getId", method.getName());
        Assertions.assertEquals(uuidCurrent, method.invoke(demo));
    }

    @Test
    public void callMethodReturnSomething() {

        final UUID uuidCurrent = UUID.randomUUID();
        final Demo demo = new Demo();
        demo.setId(uuidCurrent);

        final UUID uuid = Reflection.callMethod(demo, "getId", UUID.class, new Class[]{});
        Assertions.assertNotNull(uuid);
        Assertions.assertEquals(uuidCurrent, uuid);
    }

    @Test
    public void callMethodReturnNothing() {

        final Demo demo = new Demo();

        Reflection.callMethod(demo, "setFirstName", new Class[]{String.class}, "Vanessa");
        Assertions.assertEquals("Vanessa", demo.getFirstName());
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
