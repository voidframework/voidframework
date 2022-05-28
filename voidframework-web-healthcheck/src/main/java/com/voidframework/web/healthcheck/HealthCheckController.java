package com.voidframework.web.healthcheck;

import com.google.inject.Inject;
import com.voidframework.core.bindable.Controller;
import com.voidframework.core.helper.Json;
import com.voidframework.healthcheck.Health;
import com.voidframework.healthcheck.HealthCheckManager;
import com.voidframework.web.http.Result;
import com.voidframework.web.http.param.RequestRoute;
import com.voidframework.web.routing.HttpMethod;

import java.util.Map;

@Controller
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

    @RequestRoute(method = HttpMethod.GET, route = "/healthcheck")
    public Result healthCheck() {
        final Map<String, Health> healthPerNameMap = healthCheckManager.checkHealth();
        return Result.ok(Json.toJson(healthPerNameMap));
    }
}
