package com.voidframework.cache.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.voidframework.cache.Cache;
import com.voidframework.cache.engine.CacheEngine;

public class CacheModule extends AbstractModule {

    @Override
    protected void configure() {
        final CacheInterceptor cacheInterceptor = new CacheInterceptor();

        requestInjection(cacheInterceptor);
        bind(CacheEngine.class).toProvider(CacheEngineProvider.class);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Cache.class), cacheInterceptor);
    }
}
