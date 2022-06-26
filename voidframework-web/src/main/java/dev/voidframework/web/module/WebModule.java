package dev.voidframework.web.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import dev.voidframework.web.routing.Router;
import dev.voidframework.web.routing.impl.DefaultRouter;

/**
 * The web module.
 */
public class WebModule extends AbstractModule {

    @Override
    protected void configure() {

        final Router router = new DefaultRouter();

        bind(Router.class).toInstance(router);
        bindListener(Matchers.any(), new ControllerAnnotationListener(router));
    }
}
