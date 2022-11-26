package dev.voidframework.web.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.typesafe.config.Config;
import dev.voidframework.web.http.routing.Router;
import dev.voidframework.web.http.routing.impl.DefaultRouter;

/**
 * The web module.
 */
public class WebModule extends AbstractModule {

    private Config configuration;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    public WebModule(final Config configuration) {

        this.configuration = configuration;
    }

    @Override
    protected void configure() {

        final String contextPath = this.configuration.getString("voidframework.web.contextPath");
        final Router router = new DefaultRouter();

        bind(Router.class).toInstance(router);
        bindListener(Matchers.any(), new ControllerAnnotationListener(contextPath, router));
    }
}
