package dev.voidframework.healthcheck;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Health check manager takes care of executing the various hooks defined by the
 * use of the {@link dev.voidframework.healthcheck.HealthChecker} annotation.
 *
 * @since 1.0.0
 */
public final class HealthCheckManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckManager.class);
    private static final Comparator<Class<? extends HealthChecker>> HEALTHCHECKER_COMPARATOR = Comparator.comparing(Class::getName);

    private final List<Class<? extends HealthChecker>> healthCheckerList;
    private Injector injector;

    /**
     * Build a new instance.
     *
     * @since 1.0.0
     */
    public HealthCheckManager() {

        this.healthCheckerList = new ArrayList<>();
    }

    /**
     * Sets the injector to use.
     *
     * @param injector The injector instance
     * @since 1.1.0
     */
    @Inject
    public void setInjector(final Injector injector) {

        this.injector = injector;
    }

    /**
     * Registers a new health checker.
     *
     * @param healthCheckerClassType The health checker to register
     * @since 1.0.0
     */
    public void registerHealthCheck(final Class<? extends HealthChecker> healthCheckerClassType) {

        LOGGER.debug("Register HealthCheck {}", healthCheckerClassType.getSimpleName());
        this.healthCheckerList.add(healthCheckerClassType);
    }

    /**
     * Checks the health status of the various components being monitored.
     *
     * @return The health status report
     * @since 1.0.0
     */
    public Map<String, Health> checkHealth() {

        final Map<String, Health> healthPerNameMap = new HashMap<>();

        this.healthCheckerList.stream().sorted(HEALTHCHECKER_COMPARATOR).forEach(healthCheckerClassType -> {
            final HealthChecker healthChecker = this.injector.getInstance(healthCheckerClassType);

            Health health;
            try {
                health = healthChecker.checkHealth();
            } catch (final Exception ignore) {
                health = new Health(Health.Status.DOWN, Collections.emptyMap());
            }

            healthPerNameMap.put(healthChecker.getName(), health);
        });

        return healthPerNameMap;
    }
}
