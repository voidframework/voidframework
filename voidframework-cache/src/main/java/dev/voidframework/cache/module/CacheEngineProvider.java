package dev.voidframework.cache.module;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.cache.engine.BlackHoleCacheEngine;
import dev.voidframework.cache.engine.CacheEngine;
import dev.voidframework.core.utils.ClassResolverUtils;

/**
 * Cache engine provider.
 *
 * @since 1.0.0
 */
@Singleton
public final class CacheEngineProvider implements Provider<CacheEngine> {

    private final Config configuration;
    private final Injector injector;
    private CacheEngine cacheEngine;

    /**
     * Build a new instance;
     *
     * @param configuration The application configuration
     * @param injector      The injector instance
     * @since 1.0.0
     */
    @Inject
    public CacheEngineProvider(final Config configuration, final Injector injector) {

        this.configuration = configuration;
        this.injector = injector;
    }

    @Override
    public CacheEngine get() {

        if (this.cacheEngine == null) {
            if (configuration.hasPath("voidframework.cache.engine")) {
                final String cacheEngineClassName = configuration.getString("voidframework.cache.engine");
                final Class<?> clazz = ClassResolverUtils.forName(cacheEngineClassName);
                if (clazz != null) {
                    final Injector childInjector = this.injector.createChildInjector(new AbstractModule() {

                        @Override
                        protected void configure() {

                            bind(clazz);
                        }
                    });
                    this.cacheEngine = (CacheEngine) childInjector.getInstance(clazz);
                }
            }

            if (this.cacheEngine == null) {
                this.cacheEngine = new BlackHoleCacheEngine();
            }
        }

        return this.cacheEngine;
    }
}
