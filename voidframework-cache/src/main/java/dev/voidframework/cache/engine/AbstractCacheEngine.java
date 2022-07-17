package dev.voidframework.cache.engine;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This abstract implementation provides the necessary tools for the
 * various implementations of {@link CacheEngine} to work smoothly.
 */
public abstract class AbstractCacheEngine implements CacheEngine {

    private final Kryo kryo;

    /**
     * Build a new instance.
     */
    public AbstractCacheEngine() {

        this.kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(HashSet.class);
        kryo.register(Class.class);
    }

    /**
     * Wraps an object into {@code CachedElement}.
     *
     * @param obj Object to wrap
     * @return Wrapped object
     */
    public CachedElement wrap(final Object obj) {

        final Output output = new Output(new ByteBufferOutputStream());
        final Class<?> classType = obj != null ? obj.getClass() : Object.class;

        this.kryo.writeObjectOrNull(output, obj, classType);
        return new CachedElement(classType, output.toBytes());
    }

    /**
     * Unwraps an object from {@code CachedElement}.
     *
     * @param cachedElement wrapped object
     * @return Unwrapped object
     */
    public Object unwrap(final CachedElement cachedElement) {

        if (cachedElement == null) {
            return null;
        }

        final Input input = new Input(cachedElement.content);
        return this.kryo.readObjectOrNull(input, cachedElement.classType);
    }

    /**
     * Wrapper: cached element.
     *
     * @param classType The class type
     * @param content   The value serialized
     */
    protected record CachedElement(Class<?> classType, byte[] content) {
    }
}
