package dev.voidframework.cache.engine;

import dev.voidframework.core.bindable.BindClass;

/**
 * Black Hole (do nothing) cache implementation.
 */
@BindClass
public final class BlackHoleCacheEngine implements CacheEngine {

    @Override
    public Object get(final String cacheKey) {

        return null;
    }

    @Override
    public void set(final String cacheKey, final Object value, final int timeToLive) {
    }

    @Override
    public void remove(final String cacheKey) {
    }
}
