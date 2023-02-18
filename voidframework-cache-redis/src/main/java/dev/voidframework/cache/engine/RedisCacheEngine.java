package dev.voidframework.cache.engine;

import com.google.inject.Inject;
import dev.voidframework.core.bindable.Bindable;
import dev.voidframework.redis.Redis;

/**
 * Redis cache implementation.
 *
 * @since 1.1.0
 */
@Bindable
public class RedisCacheEngine extends AbstractCacheEngine {

    private final Redis redis;

    /**
     * Build a new instance.
     *
     * @param redis The current Redis instance
     * @since 1.1.0
     */
    @Inject
    public RedisCacheEngine(final Redis redis) {

        super();
        this.redis = redis;
    }

    @Override
    public Object get(final String cacheKey) {

        final CachedElement cachedElement = this.redis.get(cacheKey, CachedElement.class);
        return this.unwrap(cachedElement);
    }

    @Override
    public void set(final String cacheKey, final Object value, final int timeToLive) {

        if (value != null) {
            final CachedElement cachedElement = this.wrap(value);
            this.redis.set(cacheKey, CachedElement.class, cachedElement, timeToLive);
        }
    }

    @Override
    public void remove(final String cacheKey) {

        this.redis.remove(cacheKey);
    }
}
