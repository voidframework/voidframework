package dev.voidframework.web.http;

import java.util.HashMap;
import java.util.Map;

/**
 * An HTTP session.
 */
public class Session extends HashMap<String, String> {

    /**
     * Indicate if the session has been modified.
     */
    transient private boolean isModified;

    /**
     * Build a new instance.
     */
    public Session() {
    }

    /**
     * Build a new instance.
     *
     * @param sessionData Initial session data
     */
    public Session(final Map<String, String> sessionData) {

        super(sessionData);
    }

    /**
     * Removes a value from the session.
     *
     * @param key Key whose mapping value to remove
     * @return The previous value associated with key, otherwise, {@code null}
     */
    @Override
    public String remove(final Object key) {

        this.isModified = true;
        return super.remove(key);
    }

    /**
     * Adds the single value to the session.
     */
    @Override
    public String put(final String key, final String value) {

        this.isModified = true;
        return super.put(key, value);
    }

    /**
     * Adds the multiple values to the session.
     */
    @Override
    public void putAll(final Map<? extends String, ? extends String> values) {

        this.isModified = true;
        super.putAll(values);
    }

    /**
     * Clears all data from the session.
     */
    @Override
    public void clear() {

        if (!this.isEmpty()) {
            this.isModified = true;
            super.clear();
        }
    }

    /**
     * Checks if the session has been modified.
     *
     * @return {@code true} if the session has been modified, otherwise, {@code false}
     */
    public boolean isModified() {

        return isModified;
    }
}
