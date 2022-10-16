package com.voidframework.redis;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.redis.Redis;
import dev.voidframework.redis.module.RedisModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class RedisTest {

    private static final String REDIS_VALID_HOSTNAME = "127.0.0.1";
    private static final int REDIS_VALID_PORT = 6379;
    private static final int TIME_TO_LIVE_IN_SECONDS = 60;

    @Test
    void cantConnectBadHostname() {

        // Arrange
        final Redis redis = this.createRedisInstance("badHostname.local.fr", REDIS_VALID_PORT);

        // Act
        final ProvisionException provisionException = Assertions.assertThrows(
            ProvisionException.class,
            () -> redis.get("voidframework.junit.item", String.class));

        // Assert
        Assertions.assertNotNull(provisionException);
        Assertions.assertNotNull(provisionException.getCause());
        Assertions.assertTrue(provisionException.getCause() instanceof JedisConnectionException);
    }

    @Test
    void cantConnectBadListenPort() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, 12345);

        // Act
        final ProvisionException provisionException = Assertions.assertThrows(
            ProvisionException.class,
            () -> redis.get("voidframework.junit.item", String.class));

        // Assert
        Assertions.assertNotNull(provisionException);
        Assertions.assertNotNull(provisionException.getCause());
        Assertions.assertTrue(provisionException.getCause() instanceof JedisConnectionException);
    }

    @Test
    void tryLockCanAcquireLock() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.lock");

        // Act
        final boolean isLockAcquired = redis.tryLock("voidframework.junit.lock", TIME_TO_LIVE_IN_SECONDS);

        // Assert
        Assertions.assertTrue(isLockAcquired);
    }

    @Test
    void tryLockCannotAcquireLock() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.tryLock("voidframework.junit.lock", 900);

        // Act
        final boolean isLockAcquired = redis.tryLock("voidframework.junit.lock", TIME_TO_LIVE_IN_SECONDS);

        // Assert
        Assertions.assertFalse(isLockAcquired);
    }

    @Test
    void existsPositive() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.set("voidframework.junit.item", String.class, "Hello World!", TIME_TO_LIVE_IN_SECONDS);

        // Act
        final boolean isItemExists = redis.exists("voidframework.junit.item");

        // Assert
        Assertions.assertTrue(isItemExists);
    }

    @Test
    void existsNegative() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        final boolean isItemExists = redis.exists("voidframework.junit.item");

        // Assert
        Assertions.assertFalse(isItemExists);
    }

    @Test
    void getConnectionDefaultDataBase() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        try (final Jedis jedisConnection = redis.getConnection()) {
            jedisConnection.set("voidframework.junit.item", "test@domain.local");
        }

        final String actualValue;
        try (final Jedis jedisConnection = redis.getConnection(0)) {
            actualValue = jedisConnection.get("voidframework.junit.item");
        }

        // Assert
        Assertions.assertEquals("test@domain.local", actualValue);
    }

    @Test
    void getConnectionDifferentDataBase() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        try (final Jedis jedisConnection = redis.getConnection()) {
            jedisConnection.set("voidframework.junit.item", "test@domain.local");
        }

        final String actualValue;
        try (final Jedis jedisConnection = redis.getConnection(1)) {
            actualValue = jedisConnection.get("voidframework.junit.item");
        }

        // Assert
        Assertions.assertNull(actualValue);
    }

    @Test
    void removeSingleElement() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.set("voidframework.junit.item", String.class, "JUnit test 'remove'");

        // Act
        redis.remove("voidframework.junit.item");
        final boolean isItemExists = redis.exists("voidframework.junit.item");

        // Assert
        Assertions.assertFalse(isItemExists);
    }

    @Test
    void removeMultipleElements() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.set("voidframework.junit.item1", String.class, "JUnit test 'remove' 1");
        redis.set("voidframework.junit.item2", String.class, "JUnit test 'remove' 2");

        // Act
        redis.remove("voidframework.junit.item1", "voidframework.junit.item2");
        final boolean isItem1Exists = redis.exists("voidframework.junit.item1");
        final boolean isItem2Exists = redis.exists("voidframework.junit.item2");

        // Assert
        Assertions.assertFalse(isItem1Exists);
        Assertions.assertFalse(isItem2Exists);
    }

    @Test
    void getOrElseItemExist() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.set("voidframework.junit.item", String.class, "JUnit test 'getOrElse'");

        // Act
        final String itemValue = redis.getOrElse("voidframework.junit.item", String.class, () -> "Not Found");

        // Assert
        Assertions.assertEquals("JUnit test 'getOrElse'", itemValue);
    }

    @Test
    void getOrElseItemDoesNotExist() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        final String itemValue = redis.getOrElse("voidframework.junit.item", String.class, () -> "Not Found");

        // Assert
        Assertions.assertEquals("Not Found", itemValue);
    }

    @Test
    void decrementFromZero() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.counter");

        // Act
        final long newCounterValue = redis.decrement("voidframework.junit.counter");

        // Assert
        Assertions.assertEquals(-1, newCounterValue);
    }

    @Test
    void decrementFromExistingValue() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.set("voidframework.junit.counter", Long.class, Long.valueOf(12));

        // Act
        final long newCounterValue = redis.decrement("voidframework.junit.counter");

        // Assert
        Assertions.assertEquals(11, newCounterValue);
    }

    @Test
    void incrementFromZero() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.counter");

        // Act
        final long newCounterValue = redis.increment("voidframework.junit.counter");

        // Assert
        Assertions.assertEquals(1, newCounterValue);
    }

    @Test
    void incrementFromExistingValue() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.set("voidframework.junit.counter", Long.class, Long.valueOf(12));

        // Act
        final long newCounterValue = redis.increment("voidframework.junit.counter");

        // Assert
        Assertions.assertEquals(13, newCounterValue);
    }

    /**
     * Creates a new Redis instance.
     *
     * @param hostname Redis server hostname
     * @param port     Redis service listen port
     * @return The newly created Redis instance
     */
    private Redis createRedisInstance(final String hostname, final int port) {

        final Config configuration = ConfigFactory.parseString("""
            voidframework.core.runInDevMode = true
            voidframework.redis.host = "%s"
            voidframework.redis.port = %d
            voidframework.redis.password = ""
            voidframework.redis.defaultDatabase = 0
            voidframework.redis.connPool.connectionTimeout = "2000 ms"
            voidframework.redis.connPool.maximumWait = "2000 ms"
            voidframework.redis.connPool.minimumIdle = 1
            voidframework.redis.connPool.maximumIdle = 2
            voidframework.redis.connPool.maximumPoolSize = 4
            """.formatted(hostname, port));

        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                install(new RedisModule());
            }
        });

        return injector.getInstance(Redis.class);
    }
}
