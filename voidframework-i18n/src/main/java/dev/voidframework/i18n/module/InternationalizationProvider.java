package dev.voidframework.i18n.module;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.core.utils.ClassResolverUtils;
import dev.voidframework.i18n.Internationalization;
import dev.voidframework.i18n.ResourceBundleInternationalization;

/**
 * Internationalization provider.
 *
 * @since 1.0.0
 */
@Singleton
public final class InternationalizationProvider implements Provider<Internationalization> {

    private final Config configuration;
    private final Injector injector;
    private Internationalization internationalization;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @param injector      The injector instance
     * @since 1.0.0
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
                final Class<?> clazz = ClassResolverUtils.forName(internationalizationImplClassName);

                if (clazz != null) {
                    final Injector childInjector = this.injector.createChildInjector(new AbstractModule() {

                        @Override
                        protected void configure() {

                            bind(clazz);
                        }
                    });
                    this.internationalization = (Internationalization) childInjector.getInstance(clazz);
                }
            }

            if (this.internationalization == null) {
                this.internationalization = new ResourceBundleInternationalization();
            }
        }

        return this.internationalization;
    }
}
