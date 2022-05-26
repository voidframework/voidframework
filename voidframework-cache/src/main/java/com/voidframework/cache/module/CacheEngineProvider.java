package com.voidframework.cache.module;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.typesafe.config.Config;
import com.voidframework.cache.engine.BlackHoleCacheEngine;
import com.voidframework.cache.engine.CacheEngine;
import com.voidframework.core.helper.ClassResolver;

public class CacheEngineProvider implements Provider<CacheEngine> {

    private final Config configuration;
    private final Injector injector;

    @Inject
    public CacheEngineProvider(final Config configuration, final Injector injector) {
        this.configuration = configuration;
        this.injector = injector;
    }

    @Override
    public CacheEngine get() {
        if (configuration.hasPath("voidframework.cache.engine")) {
            final String cacheEngineClassName = configuration.getString("voidframework.cache.engine");
            final Class<?> clazz = ClassResolver.forName(cacheEngineClassName);
            if (clazz != null) {
                return (CacheEngine) this.injector.getInstance(clazz);
            }
        }

        return new BlackHoleCacheEngine();
    }
}
