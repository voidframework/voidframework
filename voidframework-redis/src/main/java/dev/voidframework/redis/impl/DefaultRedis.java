package dev.voidframework.redis.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.core.utils.JsonUtils;
import dev.voidframework.redis.Redis;
import dev.voidframework.redis.exception.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Implementation of {@link dev.voidframework.redis.Redis}.
 */
@Singleton
public class DefaultRedis implements Redis {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRedis.class);

    private final Provider<Jedis> jedisProvider;
    private final int defaultDatabase;

    /**
     * Build a new instance.
     *
     * @param jedisProvider The Jedis resource provider
     * @param configuration The application configuration
     */
    @Inject
    public DefaultRedis(final Provider<Jedis> jedisProvider,
                        final Config configuration) {

        this.jedisProvider = jedisProvider;
        this.defaultDatabase = configuration.getInt("voidframework.redis.defaultDatabase");
    }

    @Override
    public Jedis getConnection() {

        if (this.defaultDatabase == 0) {
            return this.jedisProvider.get();
        }

        return this.getConnection(this.defaultDatabase);
    }

    @Override
    public Jedis getConnection(final int db) {

        final Jedis jedis = this.jedisProvider.get();
        jedis.select(db >= 0 ? db : this.defaultDatabase);

        return jedis;
    }

    @Override
    public <T> T get(final String key, final TypeReference<T> typeReference) {

        return this.get(key, JsonUtils.objectMapper().readerFor(typeReference));
    }

    @Override
    public <T> T get(final String key, final Class<T> clazz) {

        return this.get(key, JsonUtils.objectMapper().readerFor(clazz));
    }

    @Override
    public <T> T get(final String key, final JavaType javaType) {

        return this.get(key, JsonUtils.objectMapper().readerFor(javaType));
    }

    @Override
    public <T> void set(final String key,
                        final TypeReference<T> typeReference,
                        final T value) {

        this.set(key, typeReference, value, 0);
    }

    @Override
    public <T> void set(final String key,
                        final TypeReference<T> typeReference,
                        final T value,
                        final int expiration) {

        this.set(key, JsonUtils.objectMapper().writerFor(typeReference), value, expiration);
    }

    @Override
    public <T> void set(final String key,
                        final Class<T> clazz,
                        final T value) {

        this.set(key, clazz, value, 0);
    }

    @Override
    public <T> void set(final String key,
                        final Class<T> clazz,
                        final T value,
                        final int expiration) {

        this.set(key, JsonUtils.objectMapper().writerFor(clazz), value, expiration);
    }

    @Override
    public void set(final String key,
                    final JavaType javaType,
                    final Object value) {

        this.set(key, javaType, value, 0);
    }

    @Override
    public void set(final String key,
                    final JavaType javaType,
                    final Object value,
                    final int expiration) {

        this.set(key, JsonUtils.objectMapper().writerFor(javaType), value, expiration);
    }

    @Override
    public <T> T getOrElse(final String key,
                           final TypeReference<T> typeReference,
                           final Callable<T> block) {

        return this.getOrElse(key, typeReference, block, 0);
    }

    @Override
    public <T> T getOrElse(final String key,
                           final TypeReference<T> typeReference,
                           final Callable<T> block,
                           final int expiration) {

        return this.getOrElse(key, JsonUtils.objectMapper().readerFor(typeReference), JsonUtils.objectMapper().writerFor(typeReference), block, expiration);
    }

    @Override
    public <T> T getOrElse(final String key,
                           final Class<T> clazz,
                           final Callable<T> block) {

        return this.getOrElse(key, clazz, block, 0);
    }

    @Override
    public <T> T getOrElse(final String key,
                           final Class<T> clazz,
                           final Callable<T> block,
                           final int expiration) {

        return this.getOrElse(key, JsonUtils.objectMapper().readerFor(clazz), JsonUtils.objectMapper().writerFor(clazz), block, expiration);
    }

    @Override
    public <T> T getOrElse(final String key,
                           final JavaType javaType,
                           final Callable<T> block) {
        return this.getOrElse(key, javaType, block, 0);
    }

    @Override
    public <T> T getOrElse(final String key,
                           final JavaType javaType,
                           final Callable<T> block,
                           final int expiration) {

        return this.getOrElse(key, JsonUtils.objectMapper().readerFor(javaType), JsonUtils.objectMapper().writerFor(javaType), block, expiration);
    }

    @Override
    public void remove(final String key) {

        try (final Jedis jedis = this.getConnection()) {
            jedis.del(key);
        }
    }

    @Override
    public void remove(final String... keys) {

        try (final Jedis jedis = this.getConnection()) {
            for (final String key : keys) {
                jedis.del(key);
            }
        }
    }

    @Override
    public boolean exists(final String key) {

        final boolean exists;

        try (final Jedis jedis = this.getConnection()) {
            exists = jedis.exists(key);
        }

        return exists;
    }

    @Override
    public <T> void addInList(final String key,
                              final TypeReference<T> typeReference,
                              final Object value) {

        this.addInList(key, JsonUtils.objectMapper().writerFor(typeReference), value);
    }

    @Override
    public <T> void addInList(final String key,
                              final TypeReference<T> typeReference,
                              final Object value,
                              final int maxItem) {

        this.addInList(key, JsonUtils.objectMapper().writerFor(typeReference), value, maxItem);
    }

    @Override
    public <T> void addInList(final String key,
                              final Class<T> clazz,
                              final T value) {

        this.addInList(key, JsonUtils.objectMapper().writerFor(clazz), value);
    }

    @Override
    public <T> void addInList(final String key,
                              final Class<T> clazz,
                              final T value,
                              final int maxItem) {

        this.addInList(key, JsonUtils.objectMapper().writerFor(clazz), value, maxItem);
    }

    @Override
    public void addInList(final String key,
                          final JavaType javaType,
                          final Object value) {

        this.addInList(key, JsonUtils.objectMapper().writerFor(javaType), value);
    }

    @Override
    public void addInList(final String key,
                          final JavaType javaType,
                          final Object value,
                          final int maxItem) {

        this.addInList(key, JsonUtils.objectMapper().writerFor(javaType), value, maxItem);
    }

    @Override
    public <T> List<T> getFromList(final String key, final TypeReference<T> typeReference) {

        return this.getFromList(key, typeReference, 0, -1);
    }

    @Override
    public <T> List<T> getFromList(final String key,
                                   final TypeReference<T> typeReference,
                                   final int offset,
                                   final int count) {

        return this.getFromList(key, JsonUtils.objectMapper().readerFor(typeReference), offset, count);
    }

    @Override
    public <T> List<T> getFromList(final String key, final Class<T> clazz) {

        return this.getFromList(key, clazz, 0, -1);
    }

    @Override
    public <T> List<T> getFromList(final String key,
                                   final Class<T> clazz,
                                   final int offset,
                                   final int count) {

        return this.getFromList(key, JsonUtils.objectMapper().readerFor(clazz), offset, count);
    }

    @Override
    public <T> List<T> getFromList(final String key, final JavaType javaType) {

        return this.getFromList(key, javaType, 0, -1);
    }

    @Override
    public <T> List<T> getFromList(final String key,
                                   final JavaType javaType,
                                   final int offset,
                                   final int count) {

        return this.getFromList(key, JsonUtils.objectMapper().readerFor(javaType), offset, count);
    }

    @Override
    public boolean tryLock(final String key, final int expiration) {

        long ret = 0;

        try (final Jedis jedis = this.getConnection()) {
            ret = jedis.setnx(key, "1");
            if (ret == 1) {
                jedis.expire(key, expiration);
            }
        } catch (final JedisConnectionException | ProvisionException ex) {
            LOGGER.error("Can't connect to Redis: {}", ex.getCause().getMessage());
        } catch (final JedisDataException ex) {
            LOGGER.error("Can't connect to Redis: {}", ex.getMessage());
        }

        return ret == 1;
    }

    @Override
    public long decrement(final String key, final int expiration) {

        final long value;

        try (final Jedis jedis = this.getConnection()) {
            value = jedis.decr(key);
            if (expiration > 0 && value == -1) {
                jedis.expire(key, expiration);
            }
        }

        return value;
    }

    @Override
    public long decrement(final String key) {

        return this.decrement(key, -1);
    }

    @Override
    public long increment(final String key) {

        return this.increment(key, -1);
    }

    @Override
    public long increment(final String key, final int expiration) {

        final long value;

        try (final Jedis jedis = this.getConnection()) {
            value = jedis.incr(key);
            if (expiration > 0 && value == 1) {
                jedis.expire(key, expiration);
            }
        }

        return value;
    }

    /**
     * Adds a value in a list.
     *
     * @param key    The list key
     * @param writer The object writer
     * @param value  The value to add in the list
     */
    private void addInList(final String key,
                           final ObjectWriter writer,
                           final Object value) {

        try {
            final String data = writer.writeValueAsString(value);
            try (final Jedis jedis = this.getConnection()) {
                jedis.rpush(key, data);
            }
        } catch (final IOException ex) {
            LOGGER.error("Can't add object in list", ex);
        }
    }

    /**
     * Add a value in a list.
     *
     * @param key     The list key
     * @param writer  The object writer
     * @param value   The value to add in the list
     * @param maxItem The number of entries to keep in list
     */
    private void addInList(final String key,
                           final ObjectWriter writer,
                           final Object value,
                           final int maxItem) {

        try {
            final String data = writer.writeValueAsString(value);
            try (final Jedis jedis = this.getConnection()) {
                final long currentIdx = jedis.rpush(key, data);
                if (currentIdx > maxItem) {
                    jedis.ltrim(key, maxItem > 0 ? maxItem - 1 : maxItem + 1, -1);
                }
            }
        } catch (final IOException ex) {
            LOGGER.error("Can't add object in list", ex);
        }
    }

    /**
     * Retrieves an object by key.
     *
     * @param key    Item key
     * @param reader The object reader
     * @param <T>    Generic type of something
     * @return The object or null.
     */
    private <T> T get(final String key, final ObjectReader reader) {

        T object = null;

        try {
            final String rawData;
            try (final Jedis jedis = this.getConnection()) {
                rawData = jedis.get(key);
            }

            if (rawData != null) {
                object = reader.readValue(rawData.getBytes());
            }
        } catch (final IOException ex) {
            LOGGER.error("Can't get object", ex);
        }

        return object;
    }

    /**
     * Get values from a list.
     *
     * @param key    The list key
     * @param reader The object reader
     * @param offset From where
     * @param count  The number of items to retrieve
     * @param <T>    Generic type of something implementing {@code java.io.Serializable}
     * @return The values list
     */
    private <T> List<T> getFromList(final String key,
                                    final ObjectReader reader,
                                    final int offset,
                                    final int count) {

        final List<T> objects = new ArrayList<>();

        try {
            final List<String> rawData;
            try (final Jedis jedis = this.getConnection()) {
                final int stop = offset + count;
                rawData = jedis.lrange(key, offset, stop <= 0 ? stop : stop - 1);
            }

            if (rawData != null) {
                for (final String s : rawData) {
                    objects.add(reader.readValue(s));
                }
            }
        } catch (final IOException | NullPointerException ex) {
            LOGGER.error("Can't get object from list", ex);
        }

        return objects;
    }

    /**
     * Retrieves a value from the cache, or set it from a default
     * Callable function. The value has no expiration.
     *
     * @param key        Item key
     * @param reader     The object reader
     * @param writer     The object writer
     * @param expiration The expiration in seconds
     * @param block      Block returning value to set if key does not exist
     * @param <T>        Generic type of something
     * @return value
     */
    private <T> T getOrElse(final String key,
                            final ObjectReader reader,
                            final ObjectWriter writer,
                            final Callable<T> block,
                            final int expiration) {

        T data = this.get(key, reader);

        if (data == null) {
            try {
                data = block.call();
            } catch (final Exception ex) {
                throw new RedisException.CallableFailure(ex);
            }

            this.set(key, writer, data, expiration);
        }

        return data;
    }

    /**
     * Sets a value without expiration.
     *
     * @param key    Item key
     * @param writer The object writer
     * @param value  The value to set
     */
    private void set(final String key,
                     final ObjectWriter writer,
                     final Object value,
                     final int expiration) {

        try {
            final String data = writer.writeValueAsString(value);
            try (final Jedis jedis = this.getConnection()) {
                jedis.set(key, data);
                if (expiration > 0) {
                    jedis.expire(key, expiration);
                }
            }
        } catch (final IOException ex) {
            LOGGER.error("Can't set object", ex);
        }
    }
}
