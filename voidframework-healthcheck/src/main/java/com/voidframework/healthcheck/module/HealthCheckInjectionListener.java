package com.voidframework.healthcheck.module;

import com.google.inject.spi.InjectionListener;
import com.voidframework.healthcheck.HealthCheckManager;
import com.voidframework.healthcheck.HealthChecker;

/**
 * Listen for injections into instances.
 *
 * @param <INSTANCE_TYPE> Injected instance type
 */
public final class HealthCheckInjectionListener<INSTANCE_TYPE> implements InjectionListener<INSTANCE_TYPE> {

    private final HealthCheckManager healthCheckManager;

    /**
     * Build a new instance.
     *
     * @param healthCheckManager The health check manager to use
     */
    public HealthCheckInjectionListener(final HealthCheckManager healthCheckManager) {
        this.healthCheckManager = healthCheckManager;
    }

    @Override
    public void afterInjection(final INSTANCE_TYPE injectee) {
        this.healthCheckManager.registerHealthCheck((HealthChecker) injectee);
    }
}
