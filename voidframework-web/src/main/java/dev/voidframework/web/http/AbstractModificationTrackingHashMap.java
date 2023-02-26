package dev.voidframework.web.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Extends a standard {@link HashMap} to add ability to detect changes.
 *
 * @since 1.2.0
 */
abstract class AbstractModificationTrackingHashMap extends HashMap<String, String> {

    /**
     * Indicate if internal HashMap has been modified.
     *
     * @since 1.2.0
     */
    protected transient boolean isModified;

    /**
     * Build a new instance.
     *
     * @since 1.2.0
     */
    protected AbstractModificationTrackingHashMap() {
    }

    /**
     * Build a new instance.
     *
     * @param data Initial data
     * @since 1.2.0
     */
    protected AbstractModificationTrackingHashMap(final Map<String, String> data) {

        super(data);
    }

    /**
     * Removes a value.
     *
     * @param key Key whose mapping value to remove
     * @return The previous value associated with key, otherwise, {@code null}
     * @since 1.2.0
     */
    @Override
    public String remove(final Object key) {

        this.isModified = true;
        return super.remove(key);
    }

    /**
     * Adds a single value.
     *
     * @since 1.2.0
     */
    @Override
    public String put(final String key, final String value) {

        this.isModified = true;
        return super.put(key, value);
    }

    /**
     * Adds multiple value.
     *
     * @since 1.2.0
     */
    @Override
    public void putAll(final Map<? extends String, ? extends String> values) {

        this.isModified = true;
        super.putAll(values);
    }

    /**
     * Clears all values.
     *
     * @since 1.2.0
     */
    @Override
    public void clear() {

        if (!this.isEmpty()) {
            this.isModified = true;
            super.clear();
        }
    }

    /**
     * Checks if internal values has been modified.
     *
     * @return {@code true} if internal values has been modified, otherwise, {@code false}
     * @since 1.2.0
     */
    public boolean isModified() {

        return isModified;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final AbstractModificationTrackingHashMap that = (AbstractModificationTrackingHashMap) o;
        return isModified == that.isModified;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), isModified);
    }
}
