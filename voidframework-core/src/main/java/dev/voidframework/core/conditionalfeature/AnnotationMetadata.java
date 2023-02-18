package dev.voidframework.core.conditionalfeature;

import java.util.Collections;
import java.util.Map;

/**
 * Annotation metadata.
 *
 * @since 1.5.0
 */
public final class AnnotationMetadata {

    /**
     * Empty instance of annotation metadata.
     *
     * @since 1.5.0
     */
    public static final AnnotationMetadata EMPTY = new AnnotationMetadata(Collections.emptyMap());

    private final Map<String, Object> internalMap;

    /**
     * Build a new instance.
     *
     * @param initialData Initial data
     * @since 1.5.0
     */
    AnnotationMetadata(final Map<String, Object> initialData) {

        this.internalMap = initialData;
    }

    /**
     * Gets value as boolean.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    public boolean getBoolean(final String key) {

        return (boolean) this.internalMap.get(key);
    }

    /**
     * Gets value as boolean array.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    public boolean[] getBooleanArray(final String key) {

        return (boolean[]) this.internalMap.get(key);
    }

    /**
     * Gets value as char.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    public char getChar(final String key) {

        return (char) this.internalMap.get(key);
    }

    /**
     * Gets value as char array.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    public char[] getCharArray(final String key) {

        return (char[]) this.internalMap.get(key);
    }

    /**
     * Gets value as class type.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> getClassType(final String key) {

        return (Class<T>) this.internalMap.get(key);
    }

    /**
     * Gets value as class type array.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T>[] getClassTypeArray(final String key) {

        return (Class<T>[]) this.internalMap.get(key);
    }

    /**
     * Gets value as enumeration.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> T getEnumeration(final String key) {

        return (T) this.internalMap.get(key);
    }

    /**
     * Gets value as enumeration array.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> T[] getEnumerationArray(final String key) {

        return (T[]) this.internalMap.get(key);
    }

    /**
     * Gets value as integer.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    public int getInteger(final String key) {

        return (int) this.internalMap.get(key);
    }

    /**
     * Gets value as integer array.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    public int[] getIntegerArray(final String key) {

        return (int[]) this.internalMap.get(key);
    }

    /**
     * Gets value as long.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    public long getLong(final String key) {

        return (long) this.internalMap.get(key);
    }

    /**
     * Gets value as long array.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    public long[] getLongArray(final String key) {

        return (long[]) this.internalMap.get(key);
    }

    /**
     * Gets value as string.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    public String getString(final String key) {

        return (String) this.internalMap.get(key);
    }

    /**
     * Gets value as string array.
     *
     * @param key Key whose associated value is to be returned
     * @return Value to which the specified key is mapped, otherwise {@code null}
     * @since 1.5.0
     */
    public String[] getStringArray(final String key) {

        return (String[]) this.internalMap.get(key);
    }
}
