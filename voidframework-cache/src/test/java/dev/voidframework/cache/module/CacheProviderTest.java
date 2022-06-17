package dev.voidframework.cache.module;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.cache.engine.BlackHoleCacheEngine;
import dev.voidframework.cache.engine.CacheEngine;
import dev.voidframework.cache.engine.MemoryCacheEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class CacheProviderTest {

    @Test
    public void injectorExist() {
        final Config configuration = ConfigFactory.parseString("""
            voidframework.cache.engine = "dev.voidframework.cache.engine.MemoryCacheEngine"
            voidframework.cache.inMemory.flushWhenFullMaxItem = 500
            """);
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                install(new CacheModule());
            }
        });

        final CacheEngine cacheEngine = injector.getInstance(CacheEngine.class);

        Assertions.assertNotNull(cacheEngine);
        Assertions.assertTrue(cacheEngine instanceof MemoryCacheEngine);
    }

    @Test
    public void injectorFallbackToPassThrough() {
        final Config configuration = ConfigFactory.parseString(
            "voidframework.cache.engine = dev.voidframework.cache.engine.UnknownImplementationClass");
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                install(new CacheModule());
            }
        });

        final CacheEngine cacheEngine = injector.getInstance(CacheEngine.class);

        Assertions.assertNotNull(cacheEngine);
        Assertions.assertTrue(cacheEngine instanceof BlackHoleCacheEngine);
    }
}
