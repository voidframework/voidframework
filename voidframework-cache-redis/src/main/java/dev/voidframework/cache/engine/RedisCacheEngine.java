package dev.voidframework.cache.engine;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.inject.Inject;
import dev.voidframework.core.bindable.BindClass;
import dev.voidframework.redis.Redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Redis cache implementation.
 */
@BindClass
public class RedisCacheEngine implements CacheEngine {

    private final Redis redis;
    private final Kryo kryo;

    /**
     * Build a new instance.
     *
     * @param redis The current Redis instance
     */
    @Inject
    public RedisCacheEngine(final Redis redis) {
        this.redis = redis;

        this.kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(HashSet.class);
        kryo.register(Class.class);
    }

    @Override
    public Object get(final String cacheKey) {

        final CachedElement cachedElement = this.redis.get(cacheKey, CachedElement.class);
        if (cachedElement == null) {
            return null;
        }

        final Input input = new Input(cachedElement.content);
        return this.kryo.readObjectOrNull(input, cachedElement.classType);
    }

    @Override
    public void set(final String cacheKey, final Object value, final int timeToLive) {

        if (value != null) {
            final Output output = new Output(new ByteBufferOutputStream());
            this.kryo.writeObjectOrNull(output, value, value.getClass());

            final CachedElement cachedElement = new CachedElement(value.getClass(), output.toBytes());
            this.redis.set(cacheKey, CachedElement.class, cachedElement, timeToLive);
        }
    }

    @Override
    public void remove(final String cacheKey) {

        this.redis.remove(cacheKey);
    }

    /**
     * Wrapper: cached element.
     *
     * @param classType The class type
     * @param content   The value serialized
     */
    private record CachedElement(Class<?> classType, byte[] content) {
    }
}
