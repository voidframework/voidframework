package dev.voidframework.cache.engine;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

/**
 * This abstract implementation provides the necessary tools for the
 * various implementations of {@link CacheEngine} to work smoothly.
 *
 * @since 1.1.0
 */
public abstract class AbstractCacheEngine implements CacheEngine {

    private final Kryo kryo;

    /**
     * Build a new instance.
     *
     * @since 1.1.0
     */
    protected AbstractCacheEngine() {

        this.kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.register(ArrayList.class);
        kryo.register(Class.class);
        kryo.register(HashMap.class);
        kryo.register(HashSet.class);
        kryo.register(Optional.class);
    }

    /**
     * Wraps an object into {@code CachedElement}.
     *
     * @param obj Object to wrap
     * @return Wrapped object
     * @since 1.1.0
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
     * @since 1.1.0
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
     * @since 1.1.0
     */
    protected record CachedElement(Class<?> classType, byte[] content) {

        @Override
        public boolean equals(final Object o) {

            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final CachedElement that = (CachedElement) o;
            return classType.equals(that.classType) && Arrays.equals(content, that.content);
        }

        @Override
        public int hashCode() {

            int result = Objects.hash(classType);
            result = 31 * result + Arrays.hashCode(content);
            return result;
        }

        @Override
        public String toString() {

            return "CachedElement{classType='" + classType + "', content='<binaryOfLength=" + content.length + ">'}";
        }
    }
}
