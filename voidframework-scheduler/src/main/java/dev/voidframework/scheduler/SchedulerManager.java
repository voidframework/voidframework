package dev.voidframework.scheduler;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.core.lifecycle.LifeCycleStart;
import dev.voidframework.core.lifecycle.LifeCycleStop;
import dev.voidframework.scheduler.cron.CronExpression;
import dev.voidframework.scheduler.exception.SchedulerException;
import dev.voidframework.scheduler.module.ScheduledHandlers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scheduler manager takes care of executing scheduled hooks defined by the
 * use of the {@link dev.voidframework.scheduler.Scheduled} annotations.
 */
@Singleton
public final class SchedulerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerManager.class);

    private final Injector injector;
    private final ScheduledExecutorService scheduledExecutorService;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @param injector      The injector instance
     */
    @Inject
    public SchedulerManager(final Config configuration, final Injector injector) {
        this.injector = injector;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(
            configuration.getInt("voidframework.scheduler.threadPoolSize"),
            new SchedulerThreadFactory());
    }

    /**
     * Start the scheduler.
     */
    @LifeCycleStart(priority = 600)
    @SuppressWarnings("unused")
    public void startScheduler() {
        final ScheduledHandlers scheduledHandlers = this.injector.getInstance(ScheduledHandlers.class);

        for (final ScheduledHandlers.ScheduledHandler scheduledHandler : scheduledHandlers) {
            if (StringUtils.isNotBlank(scheduledHandler.scheduledAnnotation().cron())) {
                registerCron(scheduledHandler);
            } else {
                registerDelay(scheduledHandler);
            }
        }
    }

    /**
     * Stop the scheduler.
     */
    @LifeCycleStop(priority = 1)
    @SuppressWarnings("unused")
    public void stopScheduler() {
        try {
            this.scheduledExecutorService.shutdownNow();
        } catch (final Exception exception) {
            LOGGER.error("An error occur during the scheduler termination", exception);
        }
    }

    /**
     * Register a scheduled method backed by CRON.
     *
     * @param scheduledHandler The scheduled method handler
     */
    private void registerCron(final ScheduledHandlers.ScheduledHandler scheduledHandler) {
        // Setting up the callable to use
        final CronExpression cronExpression = new CronExpression(scheduledHandler.scheduledAnnotation().cron());
        final ZoneId zoneId = ZoneId.of(scheduledHandler.scheduledAnnotation().cronZone());
        final Object classInstance = this.injector.getInstance(scheduledHandler.classType());

        LOGGER.info("Method {}::{} is scheduled using CRON expression \"{}\" (TimeZone: {})",
            scheduledHandler.classType().getName(),
            scheduledHandler.method().getName(),
            scheduledHandler.scheduledAnnotation().cron(), zoneId);

        final Callable<Void> callable = new Callable<>() {

            @Override
            public Void call() {
                scheduledExecutorService.schedule(this, cronExpression.getNextDelayMilliseconds(zoneId), TimeUnit.MILLISECONDS);

                try {
                    scheduledHandler.method().invoke(classInstance);
                } catch (final Exception exception) {
                    LOGGER.error(
                        "An error occurred during the execution of scheduled method {}::{}",
                        scheduledHandler.getClass().getName(),
                        scheduledHandler.method().getName(),
                        exception);
                }

                return null;
            }
        };

        // Schedules the callable
        scheduledExecutorService.schedule(callable, cronExpression.getNextDelayMilliseconds(zoneId), TimeUnit.MILLISECONDS);
    }

    /**
     * Register a scheduled method backed by delay.
     *
     * @param scheduledHandler The scheduled method handler
     */
    private void registerDelay(final ScheduledHandlers.ScheduledHandler scheduledHandler) {
        // Checking the different possible options. If something goes wrong, an exception will be thrown
        if (scheduledHandler.scheduledAnnotation().fixedDelay() < 0) {
            throw new SchedulerException.InvalidFixedDelay(
                scheduledHandler.scheduledAnnotation().fixedRate());
        } else if (scheduledHandler.scheduledAnnotation().fixedRate() < 0) {
            throw new SchedulerException.InvalidFixedRate(
                scheduledHandler.scheduledAnnotation().fixedRate());
        } else if (scheduledHandler.scheduledAnnotation().initialDelay() < 0) {
            throw new SchedulerException.InvalidInitialDelay(
                scheduledHandler.scheduledAnnotation().initialDelay());
        } else if (scheduledHandler.scheduledAnnotation().fixedDelay() >= 1
            && scheduledHandler.scheduledAnnotation().fixedRate() >= 1) {
            throw new SchedulerException.FixedDelayAndRateAreExclusive();
        }

        // Setting up the callable to use
        final Object classInstance = this.injector.getInstance(scheduledHandler.classType());
        final int initialDelay = scheduledHandler.scheduledAnnotation().initialDelay() > 0
            ? scheduledHandler.scheduledAnnotation().initialDelay()
            : scheduledHandler.scheduledAnnotation().fixedRate();

        LOGGER.info("Method {}::{} is scheduled to be run every {} seconds{}",
            scheduledHandler.classType().getName(),
            scheduledHandler.method().getName(),
            scheduledHandler.scheduledAnnotation().fixedDelay() >= 1
                ? scheduledHandler.scheduledAnnotation().fixedDelay()
                : scheduledHandler.scheduledAnnotation().fixedRate(),
            scheduledHandler.scheduledAnnotation().initialDelay() > 0
                ? " after an initial delay of %d seconds".formatted(scheduledHandler.scheduledAnnotation().initialDelay())
                : StringUtils.EMPTY);

        final Callable<Void> callable;
        if (scheduledHandler.scheduledAnnotation().fixedDelay() >= 1) {
            // In mode "fixed delay", the next run is determined after the current run
            callable = new Callable<>() {

                @Override
                public Void call() {
                    try {
                        scheduledHandler.method().invoke(classInstance);
                    } catch (final Exception exception) {
                        LOGGER.error(
                            "An error occurred during the execution of scheduled method {}::{}",
                            scheduledHandler.getClass().getName(),
                            scheduledHandler.method().getName(),
                            exception);
                    }

                    scheduledExecutorService.schedule(this, scheduledHandler.scheduledAnnotation().fixedDelay(), TimeUnit.MILLISECONDS);
                    return null;
                }
            };
        } else {
            // In mode "fixe rate", the next run is determined before the current run. If the current
            // execution takes too long, it will not be cancelled when the new one is executed
            callable = new Callable<>() {

                @Override
                public Void call() {
                    scheduledExecutorService.schedule(this, scheduledHandler.scheduledAnnotation().fixedRate(), TimeUnit.MILLISECONDS);

                    try {
                        scheduledHandler.method().invoke(classInstance);
                    } catch (final Exception exception) {
                        LOGGER.error(
                            "An error occurred during the execution of scheduled method {}::{}",
                            scheduledHandler.getClass().getName(),
                            scheduledHandler.method().getName(),
                            exception);
                    }

                    return null;
                }
            };
        }

        // Schedules the callable
        scheduledExecutorService.schedule(callable, initialDelay, TimeUnit.MILLISECONDS);
    }

    /**
     * Scheduler thread factory inspired from the Java default thread factory.
     */
    private static final class SchedulerThreadFactory implements ThreadFactory {

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        /**
         * Build a new instance.
         */
        public SchedulerThreadFactory() {
            this.group = Thread.currentThread().getThreadGroup();
            this.namePrefix = "scheduler-";
        }

        @Override
        @SuppressWarnings("NullableProblems")
        public Thread newThread(final Runnable runnable) {
            final Thread thread = new Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0);
            if (thread.isDaemon()) {
                thread.setDaemon(false);
            }

            if (thread.getPriority() != Thread.NORM_PRIORITY) {
                thread.setPriority(Thread.NORM_PRIORITY);
            }

            return thread;
        }
    }
}
