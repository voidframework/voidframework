package dev.voidframework.core.lifecycle;

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
            final dev.voidframework.core.lifecycle.LifeCycleStart lifeCycleStartAnnotation = method.getAnnotation(dev.voidframework.core.lifecycle.LifeCycleStart.class);
            if (lifeCycleStartAnnotation != null) {
                this.lifeCycleManager.registerStart(injectee, method, lifeCycleStartAnnotation.priority());
            }

            final dev.voidframework.core.lifecycle.LifeCycleStop lifeCycleStopAnnotation = method.getAnnotation(dev.voidframework.core.lifecycle.LifeCycleStop.class);
            if (lifeCycleStopAnnotation != null) {
                this.lifeCycleManager.registerStop(
                    injectee, method, lifeCycleStopAnnotation.priority(), lifeCycleStopAnnotation.gracefulStopTimeoutConfigKey());
            }
        }
    }
}
