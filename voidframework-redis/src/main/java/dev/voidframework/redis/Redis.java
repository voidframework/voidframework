package dev.voidframework.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Redis service give access to methods to easily use a Redis database.
 *
 * @since 1.1.0
 */
public interface Redis {

    /**
     * Gets a Redis connection from the pool.
     *
     * @return A Redis connection
     * @since 1.1.0
     */
    Jedis getConnection();

    /**
     * Gets a Redis connection from the pool pre-configured with the right database.
     * If the database number is under zero, the database "zero" will be selected.
     *
     * @param db The database number to use
     * @return A Redis connection
     * @since 1.1.0
     */
    Jedis getConnection(final int db);

    /**
     * Retrieves an object by key.
     *
     * @param key           Item key
     * @param typeReference The object type reference
     * @param <T>           Generic type of something
     * @return object or {@code null}
     * @since 1.1.0
     */
    <T> T get(final String key, final TypeReference<T> typeReference);

    /**
     * Retrieves an object by key.
     *
     * @param key   Item key
     * @param clazz The object class
     * @param <T>   Generic type of something
     * @return object or {@code null}
     * @since 1.1.0
     */
    <T> T get(final String key, final Class<T> clazz);

    /**
     * Retrieves an object by key.
     *
     * @param key      Item key
     * @param javaType The object java type
     * @param <T>      Generic type of something
     * @return object or {@code null}
     * @since 1.1.0
     */
    <T> T get(final String key, final JavaType javaType);

    /**
     * Sets a value without expiration.
     *
     * @param key           Item key
     * @param typeReference The object type reference
     * @param value         The value to set
     * @param <T>           Generic type of something
     * @since 1.1.0
     */
    <T> void set(final String key, final TypeReference<T> typeReference, final T value);

    /**
     * Sets a value with expiration.
     *
     * @param key           Item key
     * @param typeReference The object type reference
     * @param value         The value to set
     * @param expiration    expiration in seconds
     * @param <T>           Generic type of something
     * @since 1.1.0
     */
    <T> void set(final String key, final TypeReference<T> typeReference, final T value, final int expiration);

    /**
     * Sets a value without expiration.
     *
     * @param key   Item key
     * @param clazz The object class
     * @param value The value to set
     * @param <T>   Generic type of something
     * @since 1.1.0
     */
    <T> void set(final String key, final Class<T> clazz, final T value);

    /**
     * Sets a value with expiration.
     *
     * @param key        Item key
     * @param clazz      The object class
     * @param value      The value to set
     * @param expiration expiration in seconds
     * @param <T>        Generic type of something
     * @since 1.1.0
     */
    <T> void set(final String key, final Class<T> clazz, final T value, final int expiration);

    /**
     * Sets a value without expiration.
     *
     * @param key      Item key
     * @param javaType The object java type
     * @param value    The value to set
     * @since 1.1.0
     */
    void set(final String key, final JavaType javaType, final Object value);

    /**
     * Sets a value with expiration.
     *
     * @param key        Item key
     * @param javaType   The object java type
     * @param value      The value to set
     * @param expiration expiration in seconds
     * @since 1.1.0
     */
    void set(final String key, final JavaType javaType, final Object value, final int expiration);

    /**
     * Retrieves a value from the cache, or set it from a default Callable function.
     * The value has no expiration.
     *
     * @param key           Item key
     * @param typeReference The object type reference
     * @param block         block returning value to set if key does not exist
     * @param <T>           Generic type of something
     * @return value
     * @since 1.1.0
     */
    <T> T getOrElse(final String key, final TypeReference<T> typeReference, final Callable<T> block);

    /**
     * Retrieves a value from the cache, or set it from a default Callable function.
     *
     * @param <T>           Generic type of something implementing {@code java.io.Serializable}
     * @param key           Item key
     * @param typeReference The object type reference
     * @param block         block returning value to set if key does not exist
     * @param expiration    expiration period in seconds
     * @return value
     * @since 1.1.0
     */
    <T> T getOrElse(final String key, final TypeReference<T> typeReference, final Callable<T> block, final int expiration);

    /**
     * Retrieves a value from the cache, or set it from a default Callable function.
     * The value has no expiration.
     *
     * @param key   Item key
     * @param clazz The object class
     * @param block block returning value to set if key does not exist
     * @param <T>   Generic type of something
     * @return value
     * @since 1.1.0
     */
    <T> T getOrElse(final String key, final Class<T> clazz, final Callable<T> block);

    /**
     * Retrieves a value from the cache, or set it from a default Callable function.
     *
     * @param <T>        Generic type of something implementing {@code java.io.Serializable}
     * @param key        Item key
     * @param clazz      The object class
     * @param block      block returning value to set if key does not exist
     * @param expiration expiration period in seconds
     * @return value
     * @since 1.1.0
     */
    <T> T getOrElse(final String key, final Class<T> clazz, final Callable<T> block, final int expiration);

    /**
     * Retrieves a value from the cache, or set it from a default Callable function.
     * The value has no expiration.
     *
     * @param key      Item key
     * @param javaType The object java type
     * @param block    block returning value to set if key does not exist
     * @param <T>      Generic type of something
     * @return value
     * @since 1.1.0
     */
    <T> T getOrElse(final String key, final JavaType javaType, final Callable<T> block);

    /**
     * Retrieves a value from the cache, or set it from a default Callable function.
     *
     * @param <T>        Generic type of something implementing {@code java.io.Serializable}
     * @param key        Item key
     * @param javaType   The object java type
     * @param block      block returning value to set if key does not exist
     * @param expiration expiration period in seconds
     * @return value
     * @since 1.1.0
     */
    <T> T getOrElse(final String key, final JavaType javaType, final Callable<T> block, final int expiration);

    /**
     * Removes a value from the cache.
     *
     * @param key The key to remove the value for
     * @since 1.1.0
     */
    void remove(final String key);

    /**
     * Removes a value from the cache.
     *
     * @param keys Keys to remove from redis
     * @since 1.1.0
     */
    void remove(final String... keys);

    /**
     * Checks if key is present on Redis database.
     *
     * @param key The key to test
     * @return {@code true} if the key is present on Redis database
     * @since 1.1.0
     */
    boolean exists(final String key);

    /**
     * Adds a value in a list.
     *
     * @param key           The list key
     * @param typeReference The object type reference
     * @param value         The value to add in the list
     * @param <T>           Generic type of something implementing {@code java.io.Serializable}
     * @return The list size
     * @since 1.1.0
     */
    <T> long addInList(final String key, final TypeReference<T> typeReference, final Object value);

    /**
     * Adds a value in a list.
     *
     * @param key           The list key
     * @param typeReference The object type reference
     * @param value         The value to add in the list
     * @param maxItem       The number of entries to keep in list
     * @param <T>           Generic type of something implementing {@code java.io.Serializable}
     * @return The list size
     * @since 1.1.0
     */
    <T> long addInList(final String key, final TypeReference<T> typeReference, final Object value, final int maxItem);

    /**
     * Adds a value in a list.
     *
     * @param key   The list key
     * @param clazz The object class
     * @param value The value to add in the list
     * @param <T>   Generic type of something implementing {@code java.io.Serializable}
     * @return The list size
     * @since 1.1.0
     */
    <T> long addInList(final String key, final Class<T> clazz, final T value);

    /**
     * Adds a value in a list.
     *
     * @param key     The list key
     * @param clazz   The object class
     * @param value   The value to add in the list
     * @param maxItem The number of entries to keep in list
     * @param <T>     Generic type of something implementing {@code java.io.Serializable}
     * @return The list size
     * @since 1.1.0
     */
    <T> long addInList(final String key, final Class<T> clazz, final T value, final int maxItem);

    /**
     * Adds a value in a list.
     *
     * @param key      The list key
     * @param javaType The object java type
     * @param value    The value to add in the list
     * @return The list size
     * @since 1.1.0
     */
    long addInList(final String key, final JavaType javaType, final Object value);

    /**
     * Adds a value in a list.
     *
     * @param key      The list key
     * @param javaType The object java type
     * @param value    The value to add in the list
     * @param maxItem  The number of entries to keep in list
     * @return The list size
     * @since 1.1.0
     */
    long addInList(final String key, final JavaType javaType, final Object value, final int maxItem);

    /**
     * Gets values from a list.
     *
     * @param key           The list key
     * @param typeReference The object type reference
     * @param <T>           Generic type of something implementing {@code java.io.Serializable}
     * @return The values list
     * @since 1.1.0
     */
    <T> List<T> getFromList(final String key, final TypeReference<T> typeReference);

    /**
     * Gets values from a list.
     *
     * @param key           The list key
     * @param typeReference The object type reference
     * @param offset        From where
     * @param count         The number of items to retrieve
     * @param <T>           Generic type of something implementing {@code java.io.Serializable}
     * @return The values list
     * @since 1.1.0
     */
    <T> List<T> getFromList(final String key, final TypeReference<T> typeReference, final int offset, final int count);

    /**
     * Gets values from a list.
     *
     * @param key   The list key
     * @param clazz The object class
     * @param <T>   Generic type of something implementing {@code java.io.Serializable}
     * @return The values list
     * @since 1.1.0
     */
    <T> List<T> getFromList(final String key, final Class<T> clazz);

    /**
     * Gets values from a list.
     *
     * @param key    The list key
     * @param clazz  The object class
     * @param offset From where
     * @param count  The number of items to retrieve
     * @param <T>    Generic type of something implementing {@code java.io.Serializable}
     * @return The values list
     * @since 1.1.0
     */
    <T> List<T> getFromList(final String key, final Class<T> clazz, final int offset, final int count);

    /**
     * Gets values from a list.
     *
     * @param key      The list key
     * @param javaType The object java type
     * @param <T>      Generic type of something implementing {@code java.io.Serializable}
     * @return The values list
     * @since 1.1.0
     */
    <T> List<T> getFromList(final String key, final JavaType javaType);

    /**
     * Gets values from a list.
     *
     * @param key      The list key
     * @param javaType The object java type
     * @param offset   From where
     * @param count    The number of items to retrieve
     * @param <T>      Generic type of something implementing {@code java.io.Serializable}
     * @return The values list
     * @since 1.1.0
     */
    <T> List<T> getFromList(final String key, final JavaType javaType, final int offset, final int count);

    /**
     * Tries to acquire a lock. This method will return {@code false} if it can't acquire
     * lock or can't connect to Redis server.
     *
     * @param key        The lock key
     * @param expiration The lock TTL
     * @return {@code true} in case of success, otherwise, {@code false}
     * @since 1.1.0
     */
    boolean tryLock(final String key, final int expiration);

    /**
     * Decrements a number value. If key does not exist, it will be created automatically.
     * Expiration is set only when the key is created.
     *
     * @param key        The value key
     * @param expiration The value TTL
     * @return The decremented value
     * @since 1.1.0
     */
    long decrement(final String key, final int expiration);

    /**
     * Decrements a number value. If key does not exist, it will be created automatically.
     *
     * @param key The value key
     * @return The decremented value
     * @since 1.1.0
     */
    long decrement(final String key);

    /**
     * Increments an integer value. If key does not exist, it will be created automatically.
     *
     * @param key The value key
     * @return The incremented value
     * @since 1.1.0
     */
    long increment(final String key);

    /**
     * Increments a number value. If key does not exist, it will be created automatically.
     * Expiration is set only when the key is created.
     *
     * @param key        The value key
     * @param expiration The value TTL
     * @return The incremented value
     * @since 1.1.0
     */
    long increment(final String key, final int expiration);
}
