package dev.voidframework.core.lifecycle;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.matcher.Matchers;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.helper.Reflection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class LifeCycleTest {

    @Test
    public void lifeCycleHandler() {

        final Config configuration = ConfigFactory.parseString("""
            voidframework.cache.engine = "dev.voidframework.cache.engine.MemoryCacheEngine"
            voidframework.cache.inMemory.flushWhenFullMaxItem = 500
            cfg.gracefulStopTimeoutConfigKey = 500
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

        final List<?> startHandlerList = Reflection.getFieldValue(lifeCycleManager, "startHandlerList", List.class);
        Assertions.assertNotNull(startHandlerList);
        Assertions.assertEquals(1, startHandlerList.size());

        final List<?> stopHandlerList = Reflection.getFieldValue(lifeCycleManager, "stopHandlerList", List.class);
        Assertions.assertNotNull(stopHandlerList);
        Assertions.assertEquals(1, stopHandlerList.size());

        final Example exemple = injector.getInstance(Example.class);
        Assertions.assertNotNull(exemple);

        lifeCycleManager.startAll();
        Assertions.assertEquals(1, exemple.startCallCount);
        Assertions.assertEquals(0, exemple.stopCallCount);

        final long startTime = System.currentTimeMillis();
        lifeCycleManager.stopAll();
        final long stopTime = System.currentTimeMillis();
        Assertions.assertEquals(1, exemple.startCallCount);
        Assertions.assertEquals(1, exemple.stopCallCount);
        Assertions.assertTrue((stopTime - startTime) < 1000);
    }

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
            }
        }
    }
}
