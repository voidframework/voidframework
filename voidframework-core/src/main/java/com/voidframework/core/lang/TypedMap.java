package com.voidframework.core.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class TypedMap {

    private final Map<Key<?>, Object> internalMap;

    public TypedMap() {
        this.internalMap = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> T put(final Key<T> typedKey, T value) {
        return (T) this.internalMap.put(typedKey, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final Key<T> typedKey) {
        return (T) this.internalMap.get(typedKey);
    }

    @Override
    public String toString() {
        return this.internalMap.toString();
    }

    public static final class Key<T> {

        private final String keyName;

        public Key(final String keyName) {
            this.keyName = keyName;
        }

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
