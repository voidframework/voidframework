package dev.voidframework.core.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public final class ClassResolverTest {

    @Test
    public void forNameWithoutExplicitClassLoader() {

        // Act
        final Class<?> classType = ClassResolver.forName("dev.voidframework.core.helper.ClassResolver");

        // Assert
        Assertions.assertNotNull(classType);
        Assertions.assertEquals(classType, ClassResolver.class);
    }

    @Test
    public void forNameWithoutExplicitClassLoaderNotFound() {

        // Act
        final Class<?> classType = ClassResolver.forName("dev.voidframework.core.helper.Unknown");

        // Assert
        Assertions.assertNull(classType);
    }

    @Test
    public void forNameWithoutExplicitClassLoaderNull() {

        // Act
        final Class<?> classType = ClassResolver.forName(null);

        // Assert
        Assertions.assertNull(classType);
    }

    @Test
    public void forNameWithExplicitClassLoader() {

        // Act
        final Class<?> classType = ClassResolver.forName(
            "dev.voidframework.core.helper.ClassResolver",
            Thread.currentThread().getContextClassLoader());

        // Assert
        Assertions.assertNotNull(classType);
        Assertions.assertEquals(classType, ClassResolver.class);
    }

    @Test
    public void forNameWithExplicitClassLoaderNotFound() {

        // Act
        final Class<?> classType = ClassResolver.forName(
            "dev.voidframework.core.helper.Unknown",
            Thread.currentThread().getContextClassLoader());

        // Assert
        Assertions.assertNull(classType);
    }

    @Test
    public void forNameWithExplicitClassLoaderNull() {

        // Act
        final Class<?> classType = ClassResolver.forName(
            null,
            Thread.currentThread().getContextClassLoader());

        // Assert
        Assertions.assertNull(classType);
    }
}
