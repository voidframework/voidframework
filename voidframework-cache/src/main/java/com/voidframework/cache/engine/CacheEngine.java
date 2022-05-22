package com.voidframework.cache.engine;

/**
 * All cache engine must implement this interface.
 */
public interface CacheEngine {

    /**
     * Retrieve a value from cache.
     *
     * @param cacheKey The key
     * @return The value, otherwise, {@code null}
     */
    Object get(final String cacheKey);

    /**
     * Set a value to the cache.
     *
     * @param cacheKey   The key
     * @param value      The value
     * @param timeToLive Retention time (in seconds)
     */
    void set(final String cacheKey, final Object value, final int timeToLive);
}
