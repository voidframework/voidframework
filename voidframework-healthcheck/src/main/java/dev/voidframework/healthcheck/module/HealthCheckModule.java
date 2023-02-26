package dev.voidframework.healthcheck.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import dev.voidframework.healthcheck.HealthCheckManager;

/**
 * Health check module.
 *
 * @since 1.0.0
 */
public class HealthCheckModule extends AbstractModule {

    @Override
    protected void configure() {

        final HealthCheckManager healthCheckManager = new HealthCheckManager();
        requestInjection(healthCheckManager);

        bind(HealthCheckManager.class).toInstance(healthCheckManager);
        bindListener(Matchers.any(), new HealthCheckAnnotationListener(healthCheckManager));
    }
}
