package dev.voidframework.cache.module;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.cache.Cache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class CacheInterceptorTest {

    @Test
    public void interceptorBlackHoleEngine() {
        final Config configuration = ConfigFactory.parseString("voidframework.cache.engine = dev.voidframework.cache.engine.BlackHoleCacheEngine");
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                install(new dev.voidframework.cache.module.CacheModule());
            }
        });

        final Demo demo = injector.getInstance(Demo.class);

        final String contentCall1 = demo.doSomething();
        Assertions.assertNotNull(contentCall1);
        Assertions.assertFalse(contentCall1.isBlank());

        final String contentCall2 = demo.doSomething();
        Assertions.assertNotNull(contentCall2);
        Assertions.assertFalse(contentCall2.isBlank());
        Assertions.assertNotEquals(contentCall1, contentCall2);
    }

    @Test
    public void interceptorMemoryCacheEngine() {
        final Config configuration = ConfigFactory.parseString("""
            voidframework.cache.engine = "dev.voidframework.cache.engine.MemoryCacheEngine"
            voidframework.cache.inMemory.flushWhenFullMaxItem = 500
            """);
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                install(new dev.voidframework.cache.module.CacheModule());
            }
        });

        final Demo demo = injector.getInstance(Demo.class);

        final String contentCall1 = demo.doSomething();
        Assertions.assertNotNull(contentCall1);
        Assertions.assertFalse(contentCall1.isBlank());

        final String contentCall2 = demo.doSomething();
        Assertions.assertNotNull(contentCall2);
        Assertions.assertFalse(contentCall2.isBlank());
        Assertions.assertEquals(contentCall1, contentCall2);

        final String contentCall3 = demo.doSomethingSimpleKey();
        Assertions.assertNotNull(contentCall3);
        Assertions.assertFalse(contentCall3.isBlank());
        Assertions.assertNotEquals(contentCall1, contentCall3);
        Assertions.assertNotEquals(contentCall2, contentCall3);

        final String contentCall4 = demo.doSomethingSimpleKey();
        Assertions.assertNotNull(contentCall4);
        Assertions.assertFalse(contentCall4.isBlank());
        Assertions.assertEquals(contentCall3, contentCall4);
    }

    public static class Demo {

        @Cache
        public String doSomething() {
            return UUID.randomUUID().toString();
        }

        @Cache(key = "simple.key")
        public String doSomethingSimpleKey() {
            return UUID.randomUUID().toString();
        }
    }
}
