package dev.voidframework.cache.engine;

import dev.voidframework.core.bindable.Bindable;

/**
 * Black Hole (do nothing) cache implementation.
 */
@Bindable
public final class BlackHoleCacheEngine implements CacheEngine {

    @Override
    public Object get(final String cacheKey) {

        return null;
    }

    @Override
    public void set(final String cacheKey, final Object value, final int timeToLive) {

        // Nothing to do
    }

    @Override
    public void remove(final String cacheKey) {

        // Nothing to do
    }
}
