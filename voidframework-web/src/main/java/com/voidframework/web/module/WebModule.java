package com.voidframework.web.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.voidframework.web.routing.Router;
import com.voidframework.web.routing.impl.DefaultRouter;
import com.voidframework.web.server.WebServer;

public class WebModule extends AbstractModule {

    @Override
    protected void configure() {
        final Router router = new DefaultRouter();

        bind(Router.class).toInstance(router);
        bind(WebServer.class).asEagerSingleton();
        bindListener(Matchers.any(), new ControllerAnnotationListener(router));
    }
}
