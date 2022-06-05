package dev.voidframework.core.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Typed key-value map.
 */
public final class TypedMap {

    private final Map<Key<?>, Object> internalMap;

    /**
     * Build a new instance.
     */
    public TypedMap() {
        this.internalMap = new HashMap<>();
    }

    /**
     * Associates the specified value with the specified key.
     *
     * @param typedKey The typed key
     * @param value    The value to associate with the given key
     * @param <T>      The value type
     * @return The previous value associated with key, otherwise, {@code null}
     */
    @SuppressWarnings("unchecked")
    public <T> T put(final Key<T> typedKey, T value) {
        return (T) this.internalMap.put(typedKey, value);
    }

    /**
     * Retrieves the specified value associated to the specified key.
     *
     * @param typedKey The typed key
     * @param <T>      The value type
     * @return The value associated to the specified key, otherwise, {@code null}
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final Key<T> typedKey) {
        return (T) this.internalMap.get(typedKey);
    }

    @Override
    public String toString() {
        return this.internalMap.toString();
    }

    /**
     * TypedMap Key.
     *
     * @param <T> Type of value associated to the key
     */
    public static final class Key<T> {

        private final String keyName;

        /**
         * Build a new instance.
         *
         * @param keyName The key name
         */
        private Key(final String keyName) {
            this.keyName = keyName;
        }

        /**
         * Build a new key.
         *
         * @param keyName The key name
         * @param <V>     Type of value associated to the key
         * @return The created key
         */
        public static <V> Key<V> of(final String keyName) {
            return new Key<>(keyName);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Key<?> key = (Key<?>) o;
            return Objects.equals(keyName, key.keyName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(keyName);
        }

        @Override
        public String toString() {
            return this.keyName;
        }
    }
}
