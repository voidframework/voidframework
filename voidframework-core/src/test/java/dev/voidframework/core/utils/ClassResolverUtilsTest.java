package dev.voidframework.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class ClassResolverUtilsTest {

    @Test
    void forNameWithoutExplicitClassLoader() {

        // Act
        final Class<?> classType = ClassResolverUtils.forName("dev.voidframework.core.utils.ClassResolverUtils");

        // Assert
        Assertions.assertNotNull(classType);
        Assertions.assertEquals(classType, ClassResolverUtils.class);
    }

    @Test
    void forNameWithoutExplicitClassLoaderNotFound() {

        // Act
        final Class<?> classType = ClassResolverUtils.forName("dev.voidframework.core.utils.Unknown");

        // Assert
        Assertions.assertNull(classType);
    }

    @Test
    void forNameWithoutExplicitClassLoaderNull() {

        // Act
        final Class<?> classType = ClassResolverUtils.forName(null);

        // Assert
        Assertions.assertNull(classType);
    }

    @Test
    void forNameWithExplicitClassLoader() {

        // Act
        final Class<?> classType = ClassResolverUtils.forName(
            "dev.voidframework.core.utils.ClassResolverUtils",
            Thread.currentThread().getContextClassLoader());

        // Assert
        Assertions.assertNotNull(classType);
        Assertions.assertEquals(classType, ClassResolverUtils.class);
    }

    @Test
    void forNameWithExplicitClassLoaderNotFound() {

        // Act
        final Class<?> classType = ClassResolverUtils.forName(
            "dev.voidframework.core.utils.Unknown",
            Thread.currentThread().getContextClassLoader());

        // Assert
        Assertions.assertNull(classType);
    }

    @Test
    void forNameWithExplicitClassLoaderNull() {

        // Act
        final Class<?> classType = ClassResolverUtils.forName(
            null,
            Thread.currentThread().getContextClassLoader());

        // Assert
        Assertions.assertNull(classType);
    }
}
