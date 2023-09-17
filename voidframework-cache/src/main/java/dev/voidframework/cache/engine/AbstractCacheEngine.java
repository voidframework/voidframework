package dev.voidframework.cache.engine;

import dev.voidframework.core.utils.KryoUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * This abstract implementation provides the necessary tools for the
 * various implementations of {@link CacheEngine} to work smoothly.
 *
 * @since 1.1.0
 */
public abstract class AbstractCacheEngine implements CacheEngine {

    /**
     * Build a new instance.
     *
     * @since 1.1.0
     */
    protected AbstractCacheEngine() {
    }

    /**
     * Wraps an object into {@code CachedElement}.
     *
     * @param obj Object to wrap
     * @return Wrapped object
     * @since 1.1.0
     */
    public CachedElement wrap(final Object obj) {

        final Class<?> classType = obj != null ? obj.getClass() : Object.class;
        final byte[] serializedContent = KryoUtils.serializeWithoutException(obj);

        return new CachedElement(classType, serializedContent);
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

        return KryoUtils.deserializeWithoutException(cachedElement.content, cachedElement.classType);
    }

    /**
     * Wrapper: cached element.
     *
     * @param classType The class type
     * @param content   The value serialized
     * @since 1.1.0
     */
    public record CachedElement(Class<?> classType, byte[] content) {

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
