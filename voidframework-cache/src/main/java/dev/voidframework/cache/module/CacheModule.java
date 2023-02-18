package dev.voidframework.cache.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import dev.voidframework.cache.annotation.CacheRemove;
import dev.voidframework.cache.annotation.CacheResult;
import dev.voidframework.cache.engine.CacheEngine;

/**
 * Cache module.
 *
 * @since 1.0.0
 */
public final class CacheModule extends AbstractModule {

    @Override
    protected void configure() {

        final CacheInterceptor cacheInterceptorRemove = new CacheInterceptorRemove();
        final CacheInterceptor cacheInterceptorResult = new CacheInterceptorResult();

        requestInjection(cacheInterceptorRemove);
        requestInjection(cacheInterceptorResult);
        bind(CacheEngine.class).toProvider(CacheEngineProvider.class);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(CacheRemove.class), cacheInterceptorRemove);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(CacheResult.class), cacheInterceptorResult);
    }
}
