package dev.voidframework.cache.engine;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import dev.voidframework.core.bindable.BindClass;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory cache implementation.
 */
@BindClass
public final class MemoryCacheEngine extends AbstractCacheEngine {

    private final int flushWhenFullMaxItem;
    private final Map<String, ExpirationElement> cacheMap;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
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

        final ExpirationElement expirationElement = this.cacheMap.get(cacheKey);
        if (expirationElement == null) {
            return null;
        } else if (LocalDateTime.now(ZoneOffset.UTC).isAfter(expirationElement.expirationDate)) {
            this.cacheMap.remove(cacheKey);
            return null;
        }

        return this.unwrap(expirationElement.cachedElement);
    }

    @Override
    public void set(final String cacheKey, final Object value, final int timeToLive) {

        if (StringUtils.isNotBlank(cacheKey)) {
            if (this.cacheMap.size() >= this.flushWhenFullMaxItem) {
                this.cacheMap.clear();
            }

            final ExpirationElement expirationElement = new ExpirationElement(
                this.wrap(value),
                timeToLive > 0
                    ? LocalDateTime.now(ZoneOffset.UTC).plusSeconds(timeToLive)
                    : LocalDateTime.MAX);
            this.cacheMap.put(cacheKey, expirationElement);
        }
    }

    @Override
    public void remove(final String cacheKey) {

        if (StringUtils.isNotBlank(cacheKey)) {
            this.cacheMap.remove(cacheKey);
        }
    }

    /**
     * An element with expiration.
     *
     * @param cachedElement  The cached element
     * @param expirationDate When the value will be considered as expired
     */
    private record ExpirationElement(CachedElement cachedElement,
                                     LocalDateTime expirationDate) {
    }
}
