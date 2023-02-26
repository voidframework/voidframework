package dev.voidframework.web.healthcheck;

import com.google.inject.Inject;
import dev.voidframework.core.utils.JsonUtils;
import dev.voidframework.healthcheck.Health;
import dev.voidframework.healthcheck.HealthCheckManager;
import dev.voidframework.web.bindable.WebController;
import dev.voidframework.web.http.HttpMethod;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.annotation.NoCSRF;
import dev.voidframework.web.http.annotation.RequestRoute;

import java.util.Map;

/**
 * Health check controller.
 *
 * @since 1.0.0
 */
@WebController
public final class HealthCheckController {

    private final HealthCheckManager healthCheckManager;

    /**
     * Build a new instance.
     *
     * @param healthCheckManager The health check instance
     * @since 1.0.0
     */
    @Inject
    public HealthCheckController(final HealthCheckManager healthCheckManager) {

        this.healthCheckManager = healthCheckManager;
    }

    /**
     * Retrieves all health check status.
     *
     * @return A result
     * @since 1.0.0
     */
    @NoCSRF
    @RequestRoute(method = HttpMethod.GET, route = "/healthcheck")
    public Result healthCheck() {

        final Map<String, Health> healthPerNameMap = healthCheckManager.checkHealth();
        return Result.ok(JsonUtils.toJson(healthPerNameMap));
    }
}
