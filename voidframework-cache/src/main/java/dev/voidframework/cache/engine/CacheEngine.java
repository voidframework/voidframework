package dev.voidframework.cache.engine;

/**
 * The cache engine is responsible for adding, retrieving and managing the lifetime of cached items.
 * All cache engine implementation must implements this interface.
 */
public interface CacheEngine {

    /**
     * Retrieves a value from cache.
     *
     * @param cacheKey The key
     * @return The value, otherwise, {@code null}
     */
    Object get(final String cacheKey);

    /**
     * Sets a value to the cache.
     *
     * @param cacheKey   The key
     * @param value      The value
     * @param timeToLive Retention time (in seconds)
     */
    void set(final String cacheKey, final Object value, final int timeToLive);

    /**
     * Removes a value from cache.
     *
     * @param cacheKey The key
     */
    void remove(String cacheKey);
}
