package dev.voidframework.healthcheck.checker;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import dev.voidframework.core.bindable.Bindable;
import dev.voidframework.healthcheck.Health;
import dev.voidframework.healthcheck.HealthChecker;
import redis.clients.jedis.Jedis;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Redis health checker.
 *
 * @since 1.1.0
 */
@Bindable
@Singleton
public class RedisHealthChecker implements HealthChecker {

    private final Provider<Jedis> jedisProvider;

    /**
     * Build a new instance.
     *
     * @param jedisProvider Jedis provider
     * @since 1.1.0
     */
    @Inject
    public RedisHealthChecker(final Provider<Jedis> jedisProvider) {

        this.jedisProvider = jedisProvider;
    }

    @Override
    public String getName() {

        return "Redis";
    }

    @Override
    public Health checkHealth() {

        final long startTimeMillis = System.currentTimeMillis();
        try (final Jedis jedis = this.jedisProvider.get()) {
            jedis.ping();
        }
        final long endTimeMillis = System.currentTimeMillis();

        final Map<String, Object> detailsMap = new LinkedHashMap<>();
        detailsMap.put("latency", endTimeMillis - startTimeMillis);

        return new Health(Health.Status.UP, detailsMap);
    }
}
