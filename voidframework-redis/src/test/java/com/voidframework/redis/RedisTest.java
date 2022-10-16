package com.voidframework.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.helper.Json;
import dev.voidframework.redis.Redis;
import dev.voidframework.redis.module.RedisModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class RedisTest {

    private static final String REDIS_VALID_HOSTNAME = "127.0.0.1";
    private static final int REDIS_VALID_PORT = 6379;
    private static final int TIME_TO_LIVE_IN_SECONDS = 60;

    private static final Class<String> STRING_CLASS_TYPE = String.class;
    private static final JavaType STRING_JAVA_TYPE = Json.objectMapper().constructType(String.class);
    private static final TypeReference<String> STRING_TYPE_REFERENCE = new TypeReference<>() {
    };

    @Test
    void cantConnectBadHostname() {

        // Arrange
        final Redis redis = this.createRedisInstance("badHostname.local.fr", REDIS_VALID_PORT);

        // Act
        final ProvisionException provisionException = Assertions.assertThrows(
            ProvisionException.class,
            () -> redis.get("voidframework.junit.item", STRING_CLASS_TYPE));

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
            () -> redis.get("voidframework.junit.item", STRING_CLASS_TYPE));

        // Assert
        Assertions.assertNotNull(provisionException);
        Assertions.assertNotNull(provisionException.getCause());
        Assertions.assertTrue(provisionException.getCause() instanceof JedisConnectionException);
    }

    @Test
    void setAndGetUsingClassType() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        redis.set("voidframework.junit.item", STRING_CLASS_TYPE, "Hello World!");
        final String value = redis.get("voidframework.junit.item", STRING_CLASS_TYPE);

        // Assert
        Assertions.assertEquals("Hello World!", value);
    }

    @Test
    void setAndGetUsingJavaClass() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        redis.set("voidframework.junit.item", STRING_JAVA_TYPE, "Hello World!");
        final String value = redis.get("voidframework.junit.item", STRING_JAVA_TYPE);

        // Assert
        Assertions.assertEquals("Hello World!", value);
    }

    @Test
    void setAndGetUsingTypeReference() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        redis.set("voidframework.junit.item", STRING_TYPE_REFERENCE, "Hello World!");
        final String value = redis.get("voidframework.junit.item", STRING_TYPE_REFERENCE);

        // Assert
        Assertions.assertEquals("Hello World!", value);
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
    void tryLockCantAcquireLock() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.tryLock("voidframework.junit.lock", 900);

        // Act
        final boolean isLockAcquired = redis.tryLock("voidframework.junit.lock", TIME_TO_LIVE_IN_SECONDS);

        // Assert
        Assertions.assertFalse(isLockAcquired);
    }

    @Test
    void tryLockCantConnectRedis() {

        // Arrange
        final Redis redis = this.createRedisInstance("badHostname.local.fr", REDIS_VALID_PORT);

        // Act
        final boolean isLockAcquired = redis.tryLock("voidframework.junit.lock", TIME_TO_LIVE_IN_SECONDS);

        // Assert
        Assertions.assertFalse(isLockAcquired);
    }

    @Test
    void existsPositive() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.set("voidframework.junit.item", STRING_CLASS_TYPE, "Hello World!", TIME_TO_LIVE_IN_SECONDS);

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
        redis.set("voidframework.junit.item", STRING_CLASS_TYPE, "JUnit test 'remove'");

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
        redis.set("voidframework.junit.item1", STRING_CLASS_TYPE, "JUnit test 'remove' 1");
        redis.set("voidframework.junit.item2", STRING_CLASS_TYPE, "JUnit test 'remove' 2");

        // Act
        redis.remove("voidframework.junit.item1", "voidframework.junit.item2");
        final boolean isItem1Exists = redis.exists("voidframework.junit.item1");
        final boolean isItem2Exists = redis.exists("voidframework.junit.item2");

        // Assert
        Assertions.assertFalse(isItem1Exists);
        Assertions.assertFalse(isItem2Exists);
    }

    @Test
    void getOrElseItemExistUsingClassType() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.set("voidframework.junit.item", STRING_CLASS_TYPE, "JUnit test 'getOrElse'");

        // Act
        final String itemValue = redis.getOrElse("voidframework.junit.item", STRING_CLASS_TYPE, () -> "Not Found");

        // Assert
        Assertions.assertEquals("JUnit test 'getOrElse'", itemValue);
    }

    @Test
    void getOrElseItemExistUsingJavaType() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.set("voidframework.junit.item", STRING_CLASS_TYPE, "JUnit test 'getOrElse'");

        // Act
        final String itemValue = redis.getOrElse("voidframework.junit.item", STRING_JAVA_TYPE, () -> "Not Found");

        // Assert
        Assertions.assertEquals("JUnit test 'getOrElse'", itemValue);
    }

    @Test
    void getOrElseItemExistUsingTypeReference() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.set("voidframework.junit.item", STRING_CLASS_TYPE, "JUnit test 'getOrElse'");

        // Act
        final String itemValue = redis.getOrElse("voidframework.junit.item", STRING_TYPE_REFERENCE, () -> "Not Found");

        // Assert
        Assertions.assertEquals("JUnit test 'getOrElse'", itemValue);
    }

    @Test
    void getOrElseItemDoesNotExistUsingClassType() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        final String itemValue = redis.getOrElse("voidframework.junit.item", STRING_CLASS_TYPE, () -> "Not Found");

        // Assert
        Assertions.assertEquals("Not Found", itemValue);
    }

    @Test
    void getOrElseItemDoesNotExistUsingJavaType() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        final JavaType javaTypeString = Json.objectMapper().constructType(STRING_CLASS_TYPE);
        redis.remove("voidframework.junit.item");

        // Act
        final String itemValue = redis.getOrElse("voidframework.junit.item", javaTypeString, () -> "Not Found");

        // Assert
        Assertions.assertEquals("Not Found", itemValue);
    }

    @Test
    void getOrElseItemDoesNotExistUsingTypeReference() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        final String itemValue = redis.getOrElse("voidframework.junit.item", STRING_TYPE_REFERENCE, () -> "Not Found");

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
    void decrementWithExpiration() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.counter");

        // Act
        final long newCounterValue = redis.decrement("voidframework.junit.counter", TIME_TO_LIVE_IN_SECONDS);
        final long ttl;
        try (final Jedis jedisConnecion = redis.getConnection()) {
            ttl = jedisConnecion.ttl("voidframework.junit.counter");
        }

        // Assert
        Assertions.assertEquals(-1, newCounterValue);
        Assertions.assertTrue(ttl > TIME_TO_LIVE_IN_SECONDS - 2);
    }

    @Test
    void decrementWithoutExpiration() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.counter");

        // Act
        final long newCounterValue = redis.decrement("voidframework.junit.counter");
        final long currentTimeToLive;
        try (final Jedis jedisConnection = redis.getConnection()) {
            currentTimeToLive = jedisConnection.ttl("voidframework.junit.counter");
        }

        // Assert
        Assertions.assertEquals(-1, newCounterValue);
        Assertions.assertEquals(-1, currentTimeToLive);
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
    void incrementWithExpiration() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.counter");

        // Act
        final long newCounterValue = redis.increment("voidframework.junit.counter", TIME_TO_LIVE_IN_SECONDS);
        final long ttl;
        try (final Jedis jedisConnecion = redis.getConnection()) {
            ttl = jedisConnecion.ttl("voidframework.junit.counter");
        }

        // Assert
        Assertions.assertEquals(1, newCounterValue);
        Assertions.assertTrue(ttl > TIME_TO_LIVE_IN_SECONDS - 2);
    }

    @Test
    void incrementWithoutExpiration() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.counter");

        // Act
        final long newCounterValue = redis.increment("voidframework.junit.counter");
        final long currentTimeToLive;
        try (final Jedis jedisConnection = redis.getConnection()) {
            currentTimeToLive = jedisConnection.ttl("voidframework.junit.counter");
        }

        // Assert
        Assertions.assertEquals(1, newCounterValue);
        Assertions.assertEquals(-1, currentTimeToLive);
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

    @Test
    void addInListAndGetFromListUsingClassType() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        redis.addInList("voidframework.junit.item", STRING_CLASS_TYPE, "Hello");
        redis.addInList("voidframework.junit.item", STRING_CLASS_TYPE, "World");
        final List<String> addedValue = redis.getFromList("voidframework.junit.item", STRING_CLASS_TYPE);

        // Assert
        Assertions.assertEquals(List.of("Hello", "World"), addedValue);
    }

    @Test
    void addInListMaxItemAndGetFromListUsingClassType() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        redis.addInList("voidframework.junit.item", STRING_CLASS_TYPE, "Hello", 2);
        redis.addInList("voidframework.junit.item", STRING_CLASS_TYPE, "World", 2);
        redis.addInList("voidframework.junit.item", STRING_CLASS_TYPE, "!", 2);
        final List<String> addedValue = redis.getFromList("voidframework.junit.item", STRING_CLASS_TYPE);

        // Assert
        Assertions.assertEquals(List.of("World", "!"), addedValue);
    }

    @Test
    void addInListMaxItemAndGetFromListUsingJavaType() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        redis.addInList("voidframework.junit.item", STRING_JAVA_TYPE, "Hello", 2);
        redis.addInList("voidframework.junit.item", STRING_JAVA_TYPE, "World", 2);
        redis.addInList("voidframework.junit.item", STRING_JAVA_TYPE, "!", 2);
        final List<String> addedValue = redis.getFromList("voidframework.junit.item", STRING_JAVA_TYPE);

        // Assert
        Assertions.assertEquals(List.of("World", "!"), addedValue);
    }

    @Test
    void addInListMaxItemAndGetFromListUsingTypeReference() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        redis.addInList("voidframework.junit.item", STRING_TYPE_REFERENCE, "Hello", 2);
        redis.addInList("voidframework.junit.item", STRING_TYPE_REFERENCE, "World", 2);
        redis.addInList("voidframework.junit.item", STRING_TYPE_REFERENCE, "!", 2);
        final List<String> addedValue = redis.getFromList("voidframework.junit.item", STRING_TYPE_REFERENCE);

        // Assert
        Assertions.assertEquals(List.of("World", "!"), addedValue);
    }

    @Test
    void addInListAndGetFromListMaxItemUsingClassType() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        redis.addInList("voidframework.junit.item", STRING_CLASS_TYPE, "Hello");
        redis.addInList("voidframework.junit.item", STRING_CLASS_TYPE, "World");
        redis.addInList("voidframework.junit.item", STRING_CLASS_TYPE, "!");
        redis.addInList("voidframework.junit.item", STRING_CLASS_TYPE, "?");
        final List<String> addedValue = redis.getFromList("voidframework.junit.item", STRING_CLASS_TYPE, 1, 2);

        // Assert
        Assertions.assertEquals(List.of("World", "!"), addedValue);
    }

    @Test
    void addInListAndGetFromListMaxItemUsingJavaType() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        redis.addInList("voidframework.junit.item", STRING_JAVA_TYPE, "Hello");
        redis.addInList("voidframework.junit.item", STRING_JAVA_TYPE, "World");
        redis.addInList("voidframework.junit.item", STRING_JAVA_TYPE, "!");
        redis.addInList("voidframework.junit.item", STRING_JAVA_TYPE, "?");
        final List<String> addedValue = redis.getFromList("voidframework.junit.item", STRING_JAVA_TYPE, 1, 2);

        // Assert
        Assertions.assertEquals(List.of("World", "!"), addedValue);
    }

    @Test
    void addInListAndGetFromListMaxItemUsingTypeReference() {

        // Arrange
        final Redis redis = this.createRedisInstance(REDIS_VALID_HOSTNAME, REDIS_VALID_PORT);
        redis.remove("voidframework.junit.item");

        // Act
        redis.addInList("voidframework.junit.item", STRING_TYPE_REFERENCE, "Hello");
        redis.addInList("voidframework.junit.item", STRING_TYPE_REFERENCE, "World");
        redis.addInList("voidframework.junit.item", STRING_TYPE_REFERENCE, "!");
        redis.addInList("voidframework.junit.item", STRING_TYPE_REFERENCE, "?");
        final List<String> addedValue = redis.getFromList("voidframework.junit.item", STRING_TYPE_REFERENCE, 1, 2);

        // Assert
        Assertions.assertEquals(List.of("World", "!"), addedValue);
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
