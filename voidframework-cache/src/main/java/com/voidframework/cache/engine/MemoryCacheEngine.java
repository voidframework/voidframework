package com.voidframework.cache.engine;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory cache implementation.
 */
public final class MemoryCacheEngine implements CacheEngine {

    private final int flushWhenFullMaxItem;
    private final Map<String, CachedItem> cacheMap;

    /**
     * Build a new instance.
     */
    @Inject
    public MemoryCacheEngine(final Config configuration) {
        this.flushWhenFullMaxItem = configuration.getInt("voidframework.cache.inMemory.flushWhenFullMaxItem");
        this.cacheMap = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public Object get(final String cacheKey) {
        if (StringUtils.isBlank(cacheKey)) {
            return null;
        }

        final CachedItem cachedItem = this.cacheMap.get(cacheKey);
        if (cachedItem == null) {
            return null;
        } else if (LocalDateTime.now(ZoneOffset.UTC).isAfter(cachedItem.expirationDate)) {
            this.cacheMap.remove(cacheKey);
            return null;
        }

        return cachedItem.value;
    }

    @Override
    public void set(final String cacheKey, final Object value, final int timeToLive) {
        if (StringUtils.isNotBlank(cacheKey)) {
            if (this.cacheMap.size() >= this.flushWhenFullMaxItem) {
                this.cacheMap.clear();
            }

            final CachedItem cachedItem = new CachedItem(value,
                timeToLive > 0
                    ? LocalDateTime.now(ZoneOffset.UTC).plusSeconds(timeToLive)
                    : LocalDateTime.MAX);
            this.cacheMap.put(cacheKey, cachedItem);
        }
    }

    /**
     * A cached item.
     *
     * @param value          The cached value
     * @param expirationDate When the value will be considered as expired
     */
    private record CachedItem(Object value,
                              LocalDateTime expirationDate) {
    }
}
