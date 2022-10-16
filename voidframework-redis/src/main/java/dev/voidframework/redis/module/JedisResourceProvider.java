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
import java.util.concurrent.TimeUnit;

/**
 * Jedis resource provider.
 */
@Singleton
public class JedisResourceProvider implements Provider<Jedis> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisResourceProvider.class);

    private static final String CONFIGURATION_KEY_CONNECTION_POOL_MINIMUM_IDLE = "voidframework.redis.connPool.minimumIdle";
    private static final String CONFIGURATION_KEY_CONNECTION_POOL_MAXIMUM_IDLE = "voidframework.redis.connPool.maximumIdle";
    private static final String CONFIGURATION_KEY_CONNECTION_POOL_MAXIMUM_SIZE = "voidframework.redis.connPool.maximumPoolSize";
    private static final String CONFIGURATION_KEY_CONNECTION_POOL_CONNECTION_TIMEOUT = "voidframework.redis.connPool.connectionTimeout";
    private static final String CONFIGURATION_KEY_CONNECTION_POOL_MAXIMUM_WAIT = "voidframework.redis.connPool.maximumWait";
    private static final String CONFIGURATION_KEY_HOST = "voidframework.redis.host";
    private static final String CONFIGURATION_KEY_PORT = "voidframework.redis.port";
    private static final String CONFIGURATION_KEY_PASSWORD = "voidframework.redis.password";

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
            final int poolMinIdle = this.configuration.getInt(CONFIGURATION_KEY_CONNECTION_POOL_MINIMUM_IDLE);
            final int poolMaxIdle = this.configuration.getInt(CONFIGURATION_KEY_CONNECTION_POOL_MAXIMUM_IDLE);
            final int poolMaxTotal = this.configuration.getInt(CONFIGURATION_KEY_CONNECTION_POOL_MAXIMUM_SIZE);
            final long connectionTimeout = this.configuration.getDuration(CONFIGURATION_KEY_CONNECTION_POOL_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
            final Duration maximumWait = this.configuration.getDuration(CONFIGURATION_KEY_CONNECTION_POOL_MAXIMUM_WAIT);
            final String host = this.configuration.getString(CONFIGURATION_KEY_HOST);
            final int port = this.configuration.getInt(CONFIGURATION_KEY_PORT);
            final String password = this.configuration.getString(CONFIGURATION_KEY_PASSWORD);

            // Checks configuration
            if (poolMinIdle < 0) {
                throw new RedisException.InvalidConfiguration(CONFIGURATION_KEY_CONNECTION_POOL_MINIMUM_IDLE);
            } else if (poolMaxIdle < 0) {
                throw new RedisException.InvalidConfiguration(CONFIGURATION_KEY_CONNECTION_POOL_MAXIMUM_IDLE);
            } else if (poolMaxTotal < 0) {
                throw new RedisException.InvalidConfiguration(CONFIGURATION_KEY_CONNECTION_POOL_MAXIMUM_SIZE);
            } else if (poolMinIdle > poolMaxIdle) {
                throw new RedisException.InvalidConfiguration(CONFIGURATION_KEY_CONNECTION_POOL_MINIMUM_IDLE);
            } else if (poolMaxIdle > poolMaxTotal) {
                throw new RedisException.InvalidConfiguration(CONFIGURATION_KEY_CONNECTION_POOL_MAXIMUM_IDLE);
            } else if (StringUtils.isBlank(host)) {
                throw new RedisException.InvalidConfiguration(CONFIGURATION_KEY_HOST);
            } else if (port <= 0 || port > 65535) {
                throw new RedisException.InvalidConfiguration(CONFIGURATION_KEY_PORT);
            } else if (connectionTimeout <= 0 || connectionTimeout > Integer.MAX_VALUE) {
                throw new RedisException.InvalidConfiguration(CONFIGURATION_KEY_CONNECTION_POOL_CONNECTION_TIMEOUT);
            }

            // Configure Jedis
            final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMinIdle(poolMinIdle);
            jedisPoolConfig.setMaxIdle(poolMaxIdle);
            jedisPoolConfig.setMaxTotal(poolMaxTotal);
            jedisPoolConfig.setMaxWait(maximumWait);

            if (StringUtils.isNotBlank(password)) {
                this.jedisPool = new JedisPool(jedisPoolConfig, host, port, (int) connectionTimeout, password);
            } else {
                this.jedisPool = new JedisPool(jedisPoolConfig, host, port, (int) connectionTimeout);
            }

            // Ready!
            LOGGER.info("Redis connected to redis://{}:{}", host, port);
        }

        return this.jedisPool.getResource();
    }
}
