package com.voidframework.web;

import com.google.inject.AbstractModule;
import com.voidframework.web.routing.Router;
import com.voidframework.web.routing.impl.DefaultRouter;

public class WebModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Router.class).to(DefaultRouter.class).asEagerSingleton();
        bind(WebServer.class).asEagerSingleton();
    }
}
