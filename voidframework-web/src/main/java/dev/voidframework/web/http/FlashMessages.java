package dev.voidframework.web.http;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP Flash messages.
 */
public class FlashMessages extends HashMap<String, String> {

    /**
     * Indicate if any flash message has been modified.
     */
    transient private boolean isModified;

    /**
     * Build a new instance.
     */
    public FlashMessages() {
    }

    /**
     * Build a new instance.
     *
     * @param flashMessageData Initial flash messages data
     */
    public FlashMessages(final Map<String, String> flashMessageData) {
        super(flashMessageData);
    }

    /**
     * Removes a flash message.
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
     * Adds the single flash message.
     */
    @Override
    public String put(final String key, final String value) {
        this.isModified = true;
        return super.put(key, value);
    }

    /**
     * Adds multiple flash messages.
     */
    @Override
    public void putAll(final Map<? extends String, ? extends String> values) {
        this.isModified = true;
        super.putAll(values);
    }

    /**
     * Clears all flash messages.
     */
    @Override
    public void clear() {
        this.isModified = true;
        super.clear();
    }

    /**
     * Checks if any flash message has been modified.
     *
     * @return {@code true} if any flash message has been modified, otherwise, {@code false}
     */
    public boolean isModified() {
        return isModified;
    }
}
