package dev.voidframework.web.healthcheck.module;

import com.google.inject.AbstractModule;
import dev.voidframework.web.healthcheck.HealthCheckController;

/**
 * Health check controller module.
 */
public class HealthCheckControllerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HealthCheckController.class).asEagerSingleton();
    }
}
