package dev.voidframework.core.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public final class ClassResolverTest {

    @Test
    public void forNameWithoutExplicitClassLoader() {

        final Class<?> classType = ClassResolver.forName("dev.voidframework.core.helper.ClassResolver");
        Assertions.assertNotNull(classType);
        Assertions.assertEquals(classType, ClassResolver.class);
    }

    @Test
    public void forNameWithoutExplicitClassLoaderNotFound() {

        final Class<?> classType = ClassResolver.forName("dev.voidframework.core.helper.Unknown");
        Assertions.assertNull(classType);
    }

    @Test
    public void forNameWithoutExplicitClassLoaderNull() {

        final Class<?> classType = ClassResolver.forName(null);
        Assertions.assertNull(classType);
    }

    @Test
    public void forNameWithExplicitClassLoader() {

        final Class<?> classType = ClassResolver.forName(
            "dev.voidframework.core.helper.ClassResolver",
            Thread.currentThread().getContextClassLoader());
        Assertions.assertNotNull(classType);
        Assertions.assertEquals(classType, ClassResolver.class);
    }

    @Test
    public void forNameWithExplicitClassLoaderNotFound() {

        final Class<?> classType = ClassResolver.forName(
            "dev.voidframework.core.helper.Unknown",
            Thread.currentThread().getContextClassLoader());
        Assertions.assertNull(classType);
    }

    @Test
    public void forNameWithExplicitClassLoaderNull() {

        final Class<?> classType = ClassResolver.forName(
            null,
            Thread.currentThread().getContextClassLoader());
        Assertions.assertNull(classType);
    }
}
