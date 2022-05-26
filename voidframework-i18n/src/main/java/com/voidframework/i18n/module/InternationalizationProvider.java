package com.voidframework.i18n.module;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.typesafe.config.Config;
import com.voidframework.core.helper.ClassResolver;
import com.voidframework.i18n.Internationalization;
import com.voidframework.i18n.ResourceBundleInternationalization;

/**
 * Internationalization provider.
 */
public final class InternationalizationProvider implements Provider<Internationalization> {

    private final Config configuration;
    private final Injector injector;

    @Inject
    public InternationalizationProvider(final Config configuration, final Injector injector) {
        this.configuration = configuration;
        this.injector = injector;
    }

    @Override
    public Internationalization get() {
        if (configuration.hasPath("voidframework.i18n.engine")) {
            final String internationalizationImplClassName = configuration.getString("voidframework.i18n.engine");
            final Class<?> clazz = ClassResolver.forName(internationalizationImplClassName);
            if (clazz != null) {
                return (Internationalization) this.injector.getInstance(clazz);
            }
        }

        return this.injector.getInstance(ResourceBundleInternationalization.class);
    }
}
