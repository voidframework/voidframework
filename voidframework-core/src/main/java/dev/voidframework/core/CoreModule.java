package dev.voidframework.core;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.typesafe.config.Config;
import dev.voidframework.core.conversion.Conversion;
import dev.voidframework.core.conversion.ConverterManager;
import dev.voidframework.core.conversion.impl.DefaultConversion;
import dev.voidframework.core.conversion.impl.DefaultConverterManager;
import dev.voidframework.core.lifecycle.LifeCycleAnnotationListener;
import dev.voidframework.core.lifecycle.LifeCycleManager;

/**
 * Core module.
 *
 * @since 1.9.0
 */
final class CoreModule extends AbstractModule {

    private final Config configuration;
    private final LifeCycleManager lifeCycleManager;

    /**
     * Build a new instance.
     *
     * @param configuration    The application configuration
     * @param lifeCycleManager Life cycle manager
     * @since 1.9.0
     */
    CoreModule(final Config configuration, final LifeCycleManager lifeCycleManager) {

        this.configuration = configuration;
        this.lifeCycleManager = lifeCycleManager;
    }

    @Override
    protected void configure() {

        bind(Config.class).toInstance(configuration);
        bind(ConverterManager.class).to(DefaultConverterManager.class).asEagerSingleton();
        bind(Conversion.class).to(DefaultConversion.class).asEagerSingleton();

        bindListener(Matchers.any(), new LifeCycleAnnotationListener(lifeCycleManager));

        requestInjection(lifeCycleManager);
    }
}
