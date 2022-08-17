package dev.voidframework.redis.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.redis.exception.RedisException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * Jedis resource provider.
 */
@Singleton
public class JedisResourceProvider implements Provider<Jedis> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisResourceProvider.class);

    private final Config configuration;

    private JedisPool jedisPool;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    @Inject
    public JedisResourceProvider(final Config configuration) {

        this.configuration = configuration;
    }

    @Override
    public Jedis get() {

        if (this.jedisPool == null) {
            // Retrieve configuration
            final int poolMinIdle = this.configuration.getInt("voidframework.redis.connPool.minimumIdle");
            final int poolMaxIdle = this.configuration.getInt("voidframework.redis.connPool.maximumIdle");
            final int poolMaxTotal = this.configuration.getInt("voidframework.redis.connPool.maximumPoolSize");
            final int connectionTimeout = this.configuration.getInt("voidframework.redis.connPool.connectionTimeout");
            final Duration maximumWait = this.configuration.getDuration("voidframework.redis.connPool.maximumWait");
            final String host = this.configuration.getString("voidframework.redis.host");
            final int port = this.configuration.getInt("voidframework.redis.port");
            final String password = this.configuration.getString("voidframework.redis.password");

            // Check configuration
            if (poolMinIdle < 0) {
                throw new RedisException.InvalidConfiguration("voidframework.redis.connPool.minimumIdle");
            } else if (poolMaxIdle < 0) {
                throw new RedisException.InvalidConfiguration("voidframework.redis.connPool.maximumIdle");
            } else if (poolMaxTotal < 0) {
                throw new RedisException.InvalidConfiguration("voidframework.redis.connPool.maximumPoolSize");
            } else if (poolMinIdle > poolMaxIdle) {
                throw new RedisException.InvalidConfiguration("voidframework.redis.connPool.minimumIdle");
            } else if (poolMaxIdle > poolMaxTotal) {
                throw new RedisException.InvalidConfiguration("voidframework.redis.connPool.maximumIdle");
            } else if (host.isBlank()) {
                throw new RedisException.InvalidConfiguration("voidframework.redis.host");
            } else if (port <= 0) {
                throw new RedisException.InvalidConfiguration("voidframework.redis.port");
            } else if (connectionTimeout <= 0) {
                throw new RedisException.InvalidConfiguration("voidframework.redis.connPool.connectionTimeout");
            }

            // Configure Jedis
            final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMinIdle(poolMinIdle);
            jedisPoolConfig.setMaxIdle(poolMaxIdle);
            jedisPoolConfig.setMaxTotal(poolMaxTotal);
            jedisPoolConfig.setMaxWait(maximumWait);

            if (StringUtils.isNotBlank(password)) {
                this.jedisPool = new JedisPool(jedisPoolConfig, host, port, connectionTimeout, password);
            } else {
                this.jedisPool = new JedisPool(jedisPoolConfig, host, port, connectionTimeout);
            }

            // Ready!
            LOGGER.info(
                "Redis connected to redis://{}:{}",
                this.configuration.getString("voidframework.redis.host"),
                this.configuration.getInt("voidframework.redis.port"));
        }

        return this.jedisPool.getResource();
    }
}
