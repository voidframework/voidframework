package dev.voidframework.core.helper;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Stage;
import com.google.inject.matcher.Matchers;
import org.aopalliance.intercept.Joinpoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public final class ProxyDetectorTest {

    @Test
    void isProxyClass() {

        // Arrange
        final Example proxyInstance = createProxyInstance();

        // Act
        final boolean isProxy = ProxyDetector.isProxy(proxyInstance.getClass());

        // Assert
        Assertions.assertTrue(isProxy);
    }

    @Test
    void isProxyClassNonProxyClass() {

        // Arrange
        final Example nonProxyInstance = new Example();

        // Act
        final boolean isProxy = ProxyDetector.isProxy(nonProxyInstance.getClass());

        // Assert
        Assertions.assertFalse(isProxy);
    }

    @Test
    void isProxyObject() {

        // Arrange
        final Example proxyInstance = createProxyInstance();

        // Act
        final boolean isProxy = ProxyDetector.isProxy(proxyInstance);

        // Assert
        Assertions.assertTrue(isProxy);
    }

    @Test
    void isProxyObjectNonProxyClass() {

        // Arrange
        final Example nonProxyInstance = new Example();

        // Act
        final boolean isProxy = ProxyDetector.isProxy(nonProxyInstance);

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
