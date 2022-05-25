package com.voidframework.i18n.module;

import com.google.inject.AbstractModule;
import com.voidframework.i18n.Internationalization;

/**
 * Internationalization module.
 */
public class InternationalizationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Internationalization.class).toProvider(InternationalizationProvider.class).asEagerSingleton();
    }
}
