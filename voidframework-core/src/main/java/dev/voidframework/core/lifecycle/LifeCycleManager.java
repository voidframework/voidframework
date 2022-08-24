package dev.voidframework.core.lifecycle;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import dev.voidframework.core.exception.LifeCycleException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Life cycle manager takes care of executing the various hooks defined by the use of
 * the {@link LifeCycleStart} and {@link LifeCycleStop}
 * annotations.
 */
public final class LifeCycleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(LifeCycleManager.class);

    private final Config configuration;
    private final List<StartHandler> startHandlerList;
    private final List<StopHandler> stopHandlerList;

    private Injector injector;
    private boolean isRunning;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    public LifeCycleManager(final Config configuration) {

        this.configuration = configuration;
        this.startHandlerList = new ArrayList<>();
        this.stopHandlerList = new ArrayList<>();
        this.isRunning = false;
    }

    /**
     * Sets the injector.
     *
     * @param injector The injector instance
     */
    @Inject
    public void setInjector(final Injector injector) {

        this.injector = injector;
    }

    /**
     * Register a "START" method.
     *
     * @param classType The class type where is located the method to invoke
     * @param method    The method to invoke
     * @param priority  The priority
     */
    public void registerStart(final Class<?> classType, final Method method, final int priority) {

        LOGGER.debug("Register LifeCycle 'START' {}::{} (priority={})", classType.getName(), method.getName(), priority);

        if (this.isRunning) {
            this.invokeMethodStart(new StartHandler(classType, method, priority));
        } else {
            this.startHandlerList.add(new StartHandler(classType, method, priority));
        }
    }

    /**
     * Register a "STOP" method.
     *
     * @param classType                    The class type where is located the method to invoke
     * @param method                       The method to invoke
     * @param priority                     The priority
     * @param gracefulStopTimeoutConfigKey The graceful stop timeout configuration key
     */
    public void registerStop(final Class<?> classType, final Method method, final int priority, final String gracefulStopTimeoutConfigKey) {

        LOGGER.debug("Register LifeCycle 'STOP' {}::{} (priority={})", classType.getName(), method.getName(), priority);
        this.stopHandlerList.add(new StopHandler(classType, method, priority, gracefulStopTimeoutConfigKey));
    }

    /**
     * Invoke all registered "START" methods.
     */
    public void startAll() {

        if (!this.isRunning) {
            this.isRunning = true;

            this.startHandlerList
                .stream()
                .sorted(Comparator.comparingInt(StartHandler::priority))
                .forEach(this::invokeMethodStart);
        }
    }

    /**
     * Invoke all registered "STOP" methods.
     */
    public void stopAll() {

        if (this.isRunning) {
            this.stopHandlerList
                .stream()
                .sorted(Comparator.comparingInt(StopHandler::priority))
                .forEach(this::invokeMethodStop);

            this.isRunning = false;
        }
    }

    /**
     * Invokes a "START" method.
     *
     * @param startHandler The method handler
     */
    private void invokeMethodStart(final StartHandler startHandler) {

        final Object classInstance = this.injector.getInstance(startHandler.classType);

        try {
            final long start = System.currentTimeMillis();
            startHandler.method.invoke(classInstance);
            final long end = System.currentTimeMillis();

            LOGGER.info("{}::{} executed in {}ms", classInstance.getClass().getName(), startHandler.method.getName(), end - start);
        } catch (final Throwable t) {
            throw new LifeCycleException.InvocationFailure(classInstance.getClass().getName(), startHandler.method.getName(), t);
        }
    }

    /**
     * Invokes a "STOP" method.
     *
     * @param stopHandler The method handler
     */
    private void invokeMethodStop(final StopHandler stopHandler) {

        final Object classInstance = this.injector.getInstance(stopHandler.classType);

        try {
            final Thread thread = new Thread(() -> {
                try {
                    stopHandler.method.invoke(classInstance);
                } catch (final Throwable t) {
                    LOGGER.error("Can't invoke {}::{}", classInstance.getClass().getName(), stopHandler.method.getName(), t);
                }
            });

            long gracefulStopTimeout = 0;
            if (StringUtils.isNotBlank(stopHandler.gracefulStopTimeoutConfigKey)
                && this.configuration.hasPath(stopHandler.gracefulStopTimeoutConfigKey)) {

                gracefulStopTimeout = this.configuration.getLong(stopHandler.gracefulStopTimeoutConfigKey);
            }

            final long start = System.currentTimeMillis();
            thread.setName("LifeCycle");
            thread.start();
            thread.join(gracefulStopTimeout);
            final long end = System.currentTimeMillis();

            LOGGER.info("{}::{} executed in {}ms", classInstance.getClass().getName(), stopHandler.method.getName(), end - start);
        } catch (final InterruptedException e) {
            LOGGER.info("{}::{} INTERRUPTED!", classInstance.getClass().getName(), stopHandler.method.getName());
        }
    }

    /**
     * "START" method handler.
     *
     * @param classType The class type
     * @param method    The method to invoke
     * @param priority  The priority
     */
    private record StartHandler(Class<?> classType,
                                Method method,
                                int priority) {
    }

    /**
     * "STOP" method handler.
     *
     * @param classType                    The class type
     * @param method                       The method to invoke
     * @param priority                     The priority
     * @param gracefulStopTimeoutConfigKey The graceful stop timeout configuration key
     */
    private record StopHandler(Class<?> classType,
                               Method method,
                               int priority,
                               String gracefulStopTimeoutConfigKey) {
    }
}
