package dev.voidframework.healthcheck.module;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import dev.voidframework.healthcheck.HealthCheckManager;
import dev.voidframework.healthcheck.HealthChecker;

/**
 * Detects implementations of {@link HealthChecker} when they are bind.
 */
public final class HealthCheckAnnotationListener implements TypeListener {

    private final HealthCheckManager healthCheckManager;

    /**
     * Build a new instance.
     *
     * @param healthCheckManager The health check manager to use
     */
    public HealthCheckAnnotationListener(final HealthCheckManager healthCheckManager) {

        this.healthCheckManager = healthCheckManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <CLASS_TYPE> void hear(final TypeLiteral<CLASS_TYPE> type, final TypeEncounter<CLASS_TYPE> encounter) {

        final Class<?> classType = type.getRawType();

        if (HealthChecker.class.isAssignableFrom(classType)) {
            healthCheckManager.registerHealthCheck((Class<? extends HealthChecker>) classType);
        }
    }
}
