package dev.voidframework.i18n.module;

import com.google.inject.AbstractModule;
import dev.voidframework.i18n.Internationalization;

/**
 * Internationalization module.
 */
public final class InternationalizationModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(Internationalization.class).toProvider(InternationalizationProvider.class);
    }
}
