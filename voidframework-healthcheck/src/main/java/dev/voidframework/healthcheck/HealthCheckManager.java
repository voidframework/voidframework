package dev.voidframework.healthcheck;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Health check manager takes care of executing the various hooks defined by the
 * use of the {@link dev.voidframework.healthcheck.HealthChecker} annotation.
 */
public final class HealthCheckManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckManager.class);

    private final Config configuration;
    private final List<dev.voidframework.healthcheck.HealthChecker> healthCheckerList;

    /**
     * Build a new instance.
     *
     * @param configuration The current configuration
     */
    public HealthCheckManager(final Config configuration) {
        this.configuration = configuration;
        this.healthCheckerList = new ArrayList<>();
    }

    /**
     * Registers a new health checker.
     *
     * @param healthChecker The health checker to register
     */
    public void registerHealthCheck(final dev.voidframework.healthcheck.HealthChecker healthChecker) {
        LOGGER.debug("Register HealthCheck {}", healthChecker.getName());
        this.healthCheckerList.add(healthChecker);
    }

    /**
     * Checks the health status of the various components being monitored.
     *
     * @return The health status report
     */
    public Map<String, dev.voidframework.healthcheck.Health> checkHealth() {
        final Map<String, dev.voidframework.healthcheck.Health> healthPerNameMap = new HashMap<>();

        for (final dev.voidframework.healthcheck.HealthChecker healthChecker : this.healthCheckerList) {
            final dev.voidframework.healthcheck.Health health = healthChecker.checkHealth();
            healthPerNameMap.put(healthChecker.getName(), health);
        }

        return healthPerNameMap;
    }
}
