package dev.voidframework.i18n.module;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.core.helper.ClassResolver;
import dev.voidframework.i18n.Internationalization;
import dev.voidframework.i18n.ResourceBundleInternationalization;

/**
 * Internationalization provider.
 */
@Singleton
public final class InternationalizationProvider implements Provider<Internationalization> {

    private final Config configuration;
    private final Injector injector;
    private Internationalization internationalization;

    /**
     * Build a new instance.
     *
     * @param configuration The current configuration
     * @param injector      The injector
     */
    @Inject
    public InternationalizationProvider(final Config configuration, final Injector injector) {
        this.configuration = configuration;
        this.injector = injector;
    }

    @Override
    public Internationalization get() {
        if (this.internationalization == null) {
            if (configuration.hasPath("voidframework.i18n.engine")) {
                final String internationalizationImplClassName = configuration.getString("voidframework.i18n.engine");
                final Class<?> clazz = ClassResolver.forName(internationalizationImplClassName);
                if (clazz != null) {
                    this.internationalization = (Internationalization) this.injector.getInstance(clazz);
                }
            }

            if (this.internationalization == null) {
                this.internationalization = this.injector.getInstance(ResourceBundleInternationalization.class);
            }
        }

        return this.internationalization;
    }
}
