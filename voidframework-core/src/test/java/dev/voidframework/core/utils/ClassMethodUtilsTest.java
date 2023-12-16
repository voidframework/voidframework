package dev.voidframework.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class ClassMethodUtilsTest {

    @Test
    void constructor() throws NoSuchMethodException {

        // Act
        final Constructor<ClassMethodUtils> constructor = ClassMethodUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);

        // Assert
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertEquals("This is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    void toShortSignature() throws NoSuchMethodException {

        // Arrange
        final Method method = Dummy.class.getMethod("findById", String.class);

        // Act
        final String shortSignature = ClassMethodUtils.toShortSignature(method);

        // Assert
        Assertions.assertNotNull(shortSignature);
        Assertions.assertEquals("findById(java.lang.String)", shortSignature);
    }

    @Test
    void toShortSignatureWithNullValue() {

        // Act
        final String shortSignature = ClassMethodUtils.toShortSignature(null);

        // Assert
        Assertions.assertNull(shortSignature);
    }

    /**
     * Simple dummy class.
     */
    public static class Dummy {

        public Optional<Object> findById(final String id) {

            return Optional.of(id);
        }
    }
}
