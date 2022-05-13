package com.voidframework.core;

import com.voidframework.core.routing.AppRoutesDefinition;
import com.voidframework.core.routing.HttpMethod;
import com.voidframework.core.routing.Router;

import javax.inject.Singleton;

@Singleton
public class Routes implements AppRoutesDefinition {

    @Override
    public void defineAppRoutes(final Router router) {
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/").call(Routes.class, "maSuperMethod"));
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/(?<name>[a-z]{0,36})").call(Routes.class, "maSuperMethod2"));
    }

    public String maSuperMethod() {
        return "Hello World!";
    }

    public String maSuperMethod2(final String name) {
        return "Hello World " + name + "!";
    }

    public void test() {
    }
}

