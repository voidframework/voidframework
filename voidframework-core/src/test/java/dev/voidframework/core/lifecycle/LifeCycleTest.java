package dev.voidframework.core.lifecycle;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.matcher.Matchers;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.utils.ReflectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class LifeCycleTest {

    @Test
    void lifeCycleHandlerRegister() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("""
            voidframework.cache.engine = "dev.voidframework.cache.engine.MemoryCacheEngine"
            voidframework.cache.inMemory.flushWhenFullMaxItem = 500
            cfg.gracefulStopTimeoutConfigKey = 500 ms
            """);

        final LifeCycleManager lifeCycleManager = new LifeCycleManager(configuration);

        // Act
        Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bindListener(Matchers.any(), new LifeCycleAnnotationListener(lifeCycleManager));
                requestInjection(lifeCycleManager);

                bind(Example.class).asEagerSingleton();
            }
        });

        // Assert
        final List<?> startHandlerList = ReflectionUtils.getFieldValue(lifeCycleManager, "startHandlerList", List.class);
        Assertions.assertNotNull(startHandlerList);
        Assertions.assertEquals(1, startHandlerList.size());

        final List<?> stopHandlerList = ReflectionUtils.getFieldValue(lifeCycleManager, "stopHandlerList", List.class);
        Assertions.assertNotNull(stopHandlerList);
        Assertions.assertEquals(1, stopHandlerList.size());
    }

    @Test
    void lifeCycleHandlerStartAll() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("""
            voidframework.cache.engine = "dev.voidframework.cache.engine.MemoryCacheEngine"
            voidframework.cache.inMemory.flushWhenFullMaxItem = 500
            cfg.gracefulStopTimeoutConfigKey = 500 ms
            """);

        final LifeCycleManager lifeCycleManager = new LifeCycleManager(configuration);

        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bindListener(Matchers.any(), new LifeCycleAnnotationListener(lifeCycleManager));
                requestInjection(lifeCycleManager);

                bind(Example.class).asEagerSingleton();
            }
        });

        // Act
        lifeCycleManager.startAll();

        // Assert
        final Example exemple = injector.getInstance(Example.class);
        Assertions.assertNotNull(exemple);
        Assertions.assertEquals(1, exemple.startCallCount);
        Assertions.assertEquals(0, exemple.stopCallCount);
    }

    @Test
    void lifeCycleHandlerStopAll() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("""
            voidframework.cache.engine = "dev.voidframework.cache.engine.MemoryCacheEngine"
            voidframework.cache.inMemory.flushWhenFullMaxItem = 500
            cfg.gracefulStopTimeoutConfigKey = 500 ms
            """);

        final LifeCycleManager lifeCycleManager = new LifeCycleManager(configuration);
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bindListener(Matchers.any(), new LifeCycleAnnotationListener(lifeCycleManager));
                requestInjection(lifeCycleManager);

                bind(Example.class).asEagerSingleton();
            }
        });

        ReflectionUtils.setFieldValue(lifeCycleManager, "isRunning", true);

        // Act
        final long startTime = System.currentTimeMillis();
        lifeCycleManager.stopAll();
        final long stopTime = System.currentTimeMillis();

        // Assert
        final Example exemple = injector.getInstance(Example.class);
        Assertions.assertEquals(0, exemple.startCallCount);
        Assertions.assertEquals(1, exemple.stopCallCount);
        Assertions.assertTrue((stopTime - startTime) < 1000);
    }

    /**
     * A simple class with life cycle methods.
     */
    public static class Example {

        public int startCallCount = 0;
        public int stopCallCount = 0;

        @LifeCycleStart(priority = 42)
        public void start() {

            startCallCount += 1;
        }

        @LifeCycleStop(priority = 512, gracefulStopTimeoutConfigKey = "cfg.gracefulStopTimeoutConfigKey")
        public void stop() {

            stopCallCount += 1;

            try {
                Thread.sleep(15000);
            } catch (final InterruptedException ignore) {
                // Nothing to do
            }
        }
    }
}
