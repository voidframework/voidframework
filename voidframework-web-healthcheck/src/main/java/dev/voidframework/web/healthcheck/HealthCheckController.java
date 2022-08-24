package dev.voidframework.web.healthcheck;

import com.google.inject.Inject;
import dev.voidframework.core.helper.Json;
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
 */
@WebController
public final class HealthCheckController {

    private final HealthCheckManager healthCheckManager;

    /**
     * Build a new instance.
     *
     * @param healthCheckManager The health check instance
     */
    @Inject
    public HealthCheckController(final HealthCheckManager healthCheckManager) {

        this.healthCheckManager = healthCheckManager;
    }

    /**
     * Retrieves all health check status.
     *
     * @return A result
     */
    @NoCSRF
    @RequestRoute(method = HttpMethod.GET, route = "/healthcheck")
    public Result healthCheck() {

        final Map<String, Health> healthPerNameMap = healthCheckManager.checkHealth();
        return Result.ok(Json.toJson(healthPerNameMap));
    }
}
