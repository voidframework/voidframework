package dev.voidframework.core.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Typed key-value map.
 *
 * @since 1.0.0
 */
public final class TypedMap {

    private final Map<Key<?>, Object> internalMap;

    /**
     * Build a new instance.
     *
     * @since 1.0.0
     */
    public TypedMap() {

        this.internalMap = new HashMap<>();
    }

    /**
     * Retrieves the value associated to the specified key.
     *
     * @param typedKey The typed key
     * @param <T>      The value type
     * @return The value associated to the specified key, otherwise, {@code null}
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final Key<T> typedKey) {

        return (T) this.internalMap.get(typedKey);
    }

    /**
     * Associates the specified value with the specified key.
     *
     * @param typedKey The typed key
     * @param value    The value to associate with the given key
     * @param <T>      The value type
     * @return The previous value associated with key, otherwise, {@code null}
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public <T> T put(final Key<T> typedKey, T value) {

        return (T) this.internalMap.put(typedKey, value);
    }

    /**
     * Removes the value associated to the specified key.
     *
     * @param typedKey The typed key
     * @param <T>      The value type
     * @return The value associated to the specified key, otherwise, {@code null}
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public <T> T remove(final Key<T> typedKey) {

        return (T) this.internalMap.remove(typedKey);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TypedMap typedMap = (TypedMap) o;
        return internalMap.equals(typedMap.internalMap);
    }

    @Override
    public int hashCode() {

        return Objects.hash(internalMap);
    }

    @Override
    public String toString() {

        return this.internalMap.toString();
    }

    /**
     * TypedMap Key.
     *
     * @param <T> Type of value associated to the key
     * @since 1.0.0
     */
    public static final class Key<T> {

        private final String keyName;
        private final Class<T> valueClassType;

        /**
         * Build a new instance.
         *
         * @param keyName        The key name
         * @param valueClassType The value class type
         * @since 1.0.0
         */
        private Key(final String keyName, final Class<T> valueClassType) {

            this.keyName = keyName;
            this.valueClassType = valueClassType;
        }

        /**
         * Build a new key.
         *
         * @param keyName        The key name
         * @param valueClassType The value class type
         * @param <V>            Type of value associated to the key
         * @return The created key
         * @since 1.0.0
         */
        public static <V> Key<V> of(final String keyName, final Class<V> valueClassType) {

            return new Key<>(keyName, valueClassType);
        }

        @Override
        public boolean equals(final Object o) {

            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Key<?> key = (Key<?>) o;
            return keyName.equals(key.keyName) && valueClassType.equals(key.valueClassType);
        }

        @Override
        public int hashCode() {

            return Objects.hash(keyName, valueClassType);
        }

        @Override
        public String toString() {

            return this.keyName;
        }
    }
}
