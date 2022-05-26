package com.voidframework.cache.engine;

/**
 * Black Hole (do nothing) cache implementation.
 */
public class BlackHoleCacheEngine implements CacheEngine {

    @Override
    public Object get(final String cacheKey) {
        return null;
    }

    @Override
    public void set(final String cacheKey, final Object value, final int timeToLive) {
    }
}
