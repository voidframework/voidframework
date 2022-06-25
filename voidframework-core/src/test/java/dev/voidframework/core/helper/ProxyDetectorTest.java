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

    private Exemple getProxyInstance() {

        return Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Exemple.class).asEagerSingleton();
                bindInterceptor(Matchers.any(), Matchers.any(), Joinpoint::proceed);
            }
        }).getInstance(Exemple.class);
    }

    @Test
    public void isProxyClass() {

        final Exemple nonProxyInstance = new Exemple();
        final Exemple proxyInstance = getProxyInstance();

        Assertions.assertFalse(ProxyDetector.isProxy(nonProxyInstance.getClass()));
        Assertions.assertTrue(ProxyDetector.isProxy(proxyInstance.getClass()));
    }

    @Test
    public void isProxyObject() {

        final Exemple nonProxyInstance = new Exemple();
        final Exemple proxyInstance = getProxyInstance();

        Assertions.assertFalse(ProxyDetector.isProxy(nonProxyInstance));
        Assertions.assertTrue(ProxyDetector.isProxy(proxyInstance));
    }

    public static class Exemple {
    }
}
