package dev.voidframework.cache.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import dev.voidframework.cache.Cache;
import dev.voidframework.cache.engine.CacheEngine;

/**
 * Cache module.
 */
public final class CacheModule extends AbstractModule {

    @Override
    protected void configure() {
        final CacheInterceptor cacheInterceptor = new CacheInterceptor();

        requestInjection(cacheInterceptor);
        bind(CacheEngine.class).toProvider(CacheEngineProvider.class);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Cache.class), cacheInterceptor);
    }
}
