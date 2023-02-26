package dev.voidframework.i18n.module;

import com.google.inject.AbstractModule;
import dev.voidframework.i18n.Internationalization;

/**
 * Internationalization module.
 *
 * @since 1.0.0
 */
public final class InternationalizationModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(Internationalization.class).toProvider(InternationalizationProvider.class);
    }
}
