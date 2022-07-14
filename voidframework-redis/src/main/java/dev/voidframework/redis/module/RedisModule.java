package dev.voidframework.redis.module;

import com.google.inject.AbstractModule;
import dev.voidframework.redis.Redis;
import dev.voidframework.redis.impl.DefaultRedis;
import redis.clients.jedis.Jedis;

/**
 * The Redis module.
 */
public class RedisModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(Jedis.class).toProvider(JedisResourceProvider.class);
        bind(Redis.class).to(DefaultRedis.class);
    }
}
