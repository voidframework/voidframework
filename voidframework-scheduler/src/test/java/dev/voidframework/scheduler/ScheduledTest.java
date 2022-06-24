package dev.voidframework.scheduler;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.scheduler.module.SchedulerModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.atomic.AtomicInteger;

@TestMethodOrder(MethodOrderer.MethodName.class)
public final class ScheduledTest {

    private static final AtomicInteger counterCron = new AtomicInteger(0);
    private static final AtomicInteger counterRate = new AtomicInteger(0);
    private static final AtomicInteger counterDelay = new AtomicInteger(0);

    @Test
    public void testSchedulerWithCron() {
        final Config configuration = ConfigFactory.parseString("voidframework.scheduler.threadPoolSize = 5");
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Demo.class);

                bind(Config.class).toInstance(configuration);
                install(new SchedulerModule());
            }
        });
        final SchedulerManager schedulerManager = injector.getInstance(SchedulerManager.class);

        try {
            schedulerManager.startScheduler();
            Thread.sleep(2100);
        } catch (final Exception ignore) {
        } finally {
            schedulerManager.stopScheduler();
        }

        Assertions.assertEquals(2, ScheduledTest.counterCron.get());
        Assertions.assertEquals(2, ScheduledTest.counterRate.get());
        Assertions.assertEquals(1, ScheduledTest.counterDelay.get());
    }

    public static class Demo {

        @Scheduled(cron = "* * * * * *")
        public void everySecondsCron() {
            ScheduledTest.counterCron.incrementAndGet();
        }

        @Scheduled(fixedRate = 1000)
        public void everySecondsRate() {
            ScheduledTest.counterRate.incrementAndGet();
            try {
                Thread.sleep(10000);
            } catch (final Exception ignore) {
            }
        }

        @Scheduled(fixedDelay = 1000)
        public void everySecondsDelay() {
            ScheduledTest.counterDelay.incrementAndGet();
            try {
                Thread.sleep(10000);
            } catch (final Exception ignore) {
            }
        }
    }
}
