package com.voidframework.core.lifecycle;

import com.google.inject.spi.InjectionListener;

import java.lang.reflect.Method;

/**
 * Listen for injections into instances.
 *
 * @param <INSTANCE_TYPE> Injected instance type
 */
public final class LifeCycleInjectionListener<INSTANCE_TYPE> implements InjectionListener<INSTANCE_TYPE> {

    private final LifeCycleManager lifeCycleManager;

    /**
     * Build a new instance.
     *
     * @param lifeCycleManager The life cycle manager to use
     */
    public LifeCycleInjectionListener(final LifeCycleManager lifeCycleManager) {
        this.lifeCycleManager = lifeCycleManager;
    }

    @Override
    public void afterInjection(final INSTANCE_TYPE injectee) {
        for (final Method method : injectee.getClass().getMethods()) {
            final LifeCycleStart lifeCycleStartAnnotation = method.getAnnotation(LifeCycleStart.class);
            if (lifeCycleStartAnnotation != null) {
                this.lifeCycleManager.registerStart(injectee, method, lifeCycleStartAnnotation.priority());
            }

            final LifeCycleStop lifeCycleStopAnnotation = method.getAnnotation(LifeCycleStop.class);
            if (lifeCycleStopAnnotation != null) {
                this.lifeCycleManager.registerStop(
                    injectee, method, lifeCycleStopAnnotation.priority(), lifeCycleStopAnnotation.gracefulStopTimeoutConfigKey());
            }
        }
    }
}
