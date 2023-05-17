package dev.voidframework.h2.module;

import com.google.inject.AbstractModule;
import dev.voidframework.core.conditionalfeature.RunInDevModeConditionalFeature;
import dev.voidframework.h2.H2WebConsole;

/**
 * H2 module.
 *
 * @since 1.8.0
 */
@RunInDevModeConditionalFeature
public final class H2Module extends AbstractModule {

    @Override
    protected void configure() {

        bind(H2WebConsole.class).asEagerSingleton();
    }
}
