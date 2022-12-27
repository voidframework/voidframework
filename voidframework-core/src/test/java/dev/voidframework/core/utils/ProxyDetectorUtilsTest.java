package dev.voidframework.core.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Stage;
import com.google.inject.matcher.Matchers;
import org.aopalliance.intercept.Joinpoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class ProxyDetectorUtilsTest {

    @Test
    void constructor() throws NoSuchMethodException {

        // Act
        final Constructor<ProxyDetectorUtils> constructor = ProxyDetectorUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);

        // Assert
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertEquals("This is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    void isProxyClass() {

        // Arrange
        final Example proxyInstance = createProxyInstance();

        // Act
        final boolean isProxy = ProxyDetectorUtils.isProxy(proxyInstance.getClass());

        // Assert
        Assertions.assertTrue(isProxy);
    }

    @Test
    void isProxyClassNonProxyClass() {

        // Arrange
        final Example nonProxyInstance = new Example();

        // Act
        final boolean isProxy = ProxyDetectorUtils.isProxy(nonProxyInstance.getClass());

        // Assert
        Assertions.assertFalse(isProxy);
    }

    @Test
    void isProxyObject() {

        // Arrange
        final Example proxyInstance = createProxyInstance();

        // Act
        final boolean isProxy = ProxyDetectorUtils.isProxy(proxyInstance);

        // Assert
        Assertions.assertTrue(isProxy);
    }

    @Test
    void isProxyObjectNonProxyClass() {

        // Arrange
        final Example nonProxyInstance = new Example();

        // Act
        final boolean isProxy = ProxyDetectorUtils.isProxy(nonProxyInstance);

        // Assert
        Assertions.assertFalse(isProxy);
    }

    private Example createProxyInstance() {

        return Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Example.class).asEagerSingleton();
                bindInterceptor(Matchers.any(), Matchers.any(), Joinpoint::proceed);
            }
        }).getInstance(Example.class);
    }

    /**
     * A simple class.
     */
    public static class Example {
    }
}
