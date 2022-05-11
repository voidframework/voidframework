package com.voidframework.core;

import com.voidframework.core.routing.AppRoutesDefinition;
import com.voidframework.core.routing.HttpMethod;
import com.voidframework.core.routing.Router;

public class Routes implements AppRoutesDefinition {

    @Override
    public void defineAppRoutes(final Router router) {
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/").call(Routes.class, "maSuperMethod"));
    }

    public String maSuperMethod(final String toto) {
        return null;
    }
}

