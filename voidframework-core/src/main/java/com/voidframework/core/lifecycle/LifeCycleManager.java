package com.voidframework.core.lifecycle;

import com.typesafe.config.Config;
import com.voidframework.core.exception.LifeCycleException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Life cycle manager takes care of executing the various hooks defined by the
 * use of the {@link LifeCycleStart} and {@link LifeCycleStop} annotations.
 */
public final class LifeCycleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(LifeCycleManager.class);

    private final Config configuration;
    private final List<StartHandler> startHandlerList;
    private final List<StopHandler> stopHandlerList;

    private boolean isRunning;

    /**
     * Build a new instance.
     *
     * @param configuration The current configuration
     */
    public LifeCycleManager(final Config configuration) {

        this.configuration = configuration;
        this.startHandlerList = new ArrayList<>();
        this.stopHandlerList = new ArrayList<>();
        this.isRunning = false;
    }

    public void registerStart(final Object classInstance, final Method method, final int priority) {

        LOGGER.debug("Register LifeCycle 'START' {}::{} (priority={})", classInstance.getClass().getName(), method.getName(), priority);

        if (this.isRunning) {
            invokeMethodStart(new StartHandler(classInstance, method, priority));
        } else {
            startHandlerList.add(new StartHandler(classInstance, method, priority));
        }
    }

    public void registerStop(final Object classInstance, final Method method, final int priority, final String gracefulStopTimeoutConfigKey) {

        LOGGER.debug("Register LifeCycle 'STOP' {}::{} (priority={})", classInstance.getClass().getName(), method.getName(), priority);
        stopHandlerList.add(new StopHandler(classInstance, method, priority, gracefulStopTimeoutConfigKey));
    }

    public void startAll() {

        if (!this.isRunning) {
            this.isRunning = true;

            this.startHandlerList
                .stream()
                .sorted(Comparator.comparingInt(StartHandler::priority))
                .forEach(this::invokeMethodStart);
        }
    }

    public void stopAll() {

        if (this.isRunning) {
            this.stopHandlerList
                .stream()
                .sorted(Comparator.comparingInt(StopHandler::priority))
                .forEach(this::invokeMethodStop);

            this.isRunning = false;
        }
    }

    private void invokeMethodStart(final StartHandler startHandler) {

        try {
            final long start = System.currentTimeMillis();
            startHandler.method.invoke(startHandler.classInstance);
            final long end = System.currentTimeMillis();

            LOGGER.info("{}::{} executed in {}ms", startHandler.classInstance.getClass().getName(), startHandler.method.getName(), end - start);
        } catch (final Throwable t) {
            throw new LifeCycleException.InvocationFailure(startHandler.classInstance.getClass().getName(), startHandler.method.getName(), t);
        }
    }

    private void invokeMethodStop(final StopHandler stopHandler) {

        try {
            final Thread thread = new Thread(() -> {
                try {
                    stopHandler.method.invoke(stopHandler.classInstance);
                } catch (final Throwable t) {
                    LOGGER.error("Can't invoke {}::{}", stopHandler.classInstance.getClass().getName(), stopHandler.method.getName(), t);
                }
            });

            long gracefulStopTimeout = 0;
            if (StringUtils.isNotBlank(stopHandler.gracefulStopTimeoutConfigKey)
                && this.configuration.hasPath(stopHandler.gracefulStopTimeoutConfigKey)) {

                gracefulStopTimeout = this.configuration.getLong(stopHandler.gracefulStopTimeoutConfigKey);
            }

            final long start = System.currentTimeMillis();
            thread.start();
            thread.join(gracefulStopTimeout);
            final long end = System.currentTimeMillis();

            LOGGER.info("{}::{} executed in {}ms", stopHandler.classInstance.getClass().getName(), stopHandler.method.getName(), end - start);
        } catch (final InterruptedException e) {
            LOGGER.info("{}::{} INTERRUPTED!", stopHandler.classInstance.getClass().getName(), stopHandler.method.getName());
        }
    }

    private record StartHandler(Object classInstance,
                                Method method,
                                int priority) {
    }

    private record StopHandler(Object classInstance,
                               Method method,
                               int priority,
                               String gracefulStopTimeoutConfigKey) {
    }
}
