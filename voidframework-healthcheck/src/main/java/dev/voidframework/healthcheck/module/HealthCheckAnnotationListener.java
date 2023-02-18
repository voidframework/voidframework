package dev.voidframework.healthcheck.module;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import dev.voidframework.healthcheck.HealthCheckManager;
import dev.voidframework.healthcheck.HealthChecker;

/**
 * Detects implementations of {@link HealthChecker} when they are bind.
 *
 * @since 1.0.0
 */
public final class HealthCheckAnnotationListener implements TypeListener {

    private final HealthCheckManager healthCheckManager;

    /**
     * Build a new instance.
     *
     * @param healthCheckManager The health check manager to use
     * @since 1.0.0
     */
    public HealthCheckAnnotationListener(final HealthCheckManager healthCheckManager) {

        this.healthCheckManager = healthCheckManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I> void hear(final TypeLiteral<I> type, final TypeEncounter<I> encounter) {

        final Class<?> classType = type.getRawType();

        if (HealthChecker.class.isAssignableFrom(classType)) {
            healthCheckManager.registerHealthCheck((Class<? extends HealthChecker>) classType);
        }
    }
}
