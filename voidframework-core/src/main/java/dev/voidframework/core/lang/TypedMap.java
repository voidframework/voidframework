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
     * Returns a typed map.
     *
     * @param key   The mapping key
     * @param value The mapping value
     * @param <K>   The type of the Key object
     * @param <V>   The type of the Value object
     * @return A typed map containing the specified mapping
     */
    public static <K, V extends K> TypedMap of(final Key<K> key, final V value) {

        final TypedMap typedMap = new TypedMap();
        typedMap.put(key, value);

        return typedMap;
    }

    /**
     * Returns a typed map.
     *
     * @param key1   The first mapping key
     * @param value1 The first mapping value
     * @param key2   The second mapping key
     * @param value2 The second mapping value
     * @param <K1>   The type of the first Key object
     * @param <V1>   The type of the first Value object
     * @param <K2>   The type of the second Key object
     * @param <V2>   The type of the second Value object
     * @return A typed map containing the specified mapping
     */
    public static <K1, V1 extends K1, K2, V2 extends K2>
    TypedMap of(final Key<K1> key1,
                final V1 value1,
                final Key<K2> key2,
                final V2 value2) {

        final TypedMap typedMap = new TypedMap();
        typedMap.put(key1, value1);
        typedMap.put(key2, value2);

        return typedMap;
    }

    /**
     * Returns a typed map.
     *
     * @param key1   The first mapping key
     * @param value1 The first mapping value
     * @param key2   The second mapping key
     * @param value2 The second mapping value
     * @param key3   The third mapping key
     * @param value3 The third mapping value
     * @param <K1>   The type of the first Key object
     * @param <V1>   The type of the first Value object
     * @param <K2>   The type of the second Key object
     * @param <V2>   The type of the second Value object
     * @param <K3>   The type of the third Key object
     * @param <V3>   The type of the third Value object
     * @return A typed map containing the specified mapping
     */
    public static <K1, V1 extends K1, K2, V2 extends K2, K3, V3 extends K3>
    TypedMap of(final Key<K1> key1,
                final V1 value1,
                final Key<K2> key2,
                final V2 value2,
                final Key<K3> key3,
                final V3 value3) {

        final TypedMap typedMap = new TypedMap();
        typedMap.put(key1, value1);
        typedMap.put(key2, value2);
        typedMap.put(key3, value3);

        return typedMap;
    }

    /**
     * Returns a typed map.
     *
     * @param key1   The first mapping key
     * @param value1 The first mapping value
     * @param key2   The second mapping key
     * @param value2 The second mapping value
     * @param key3   The third mapping key
     * @param value3 The third mapping value
     * @param key4   The fourth mapping key
     * @param value4 The fourth mapping value
     * @param <K1>   The type of the first Key object
     * @param <V1>   The type of the first Value object
     * @param <K2>   The type of the second Key object
     * @param <V2>   The type of the second Value object
     * @param <K3>   The type of the third Key object
     * @param <V3>   The type of the third Value object
     * @param <K4>   The type of the fourth Key object
     * @param <V4>   The type of the fourth Value object
     * @return A typed map containing the specified mapping
     */
    public static <K1, V1 extends K1, K2, V2 extends K2, K3, V3 extends K3, K4, V4 extends K4>
    TypedMap of(final Key<K1> key1,
                final V1 value1,
                final Key<K2> key2,
                final V2 value2,
                final Key<K3> key3,
                final V3 value3,
                final Key<K4> key4,
                final V4 value4) {

        final TypedMap typedMap = new TypedMap();
        typedMap.put(key1, value1);
        typedMap.put(key2, value2);
        typedMap.put(key3, value3);
        typedMap.put(key4, value4);

        return typedMap;
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
