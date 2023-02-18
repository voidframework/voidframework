package dev.voidframework.web.http;

import java.util.Map;

/**
 * HTTP Flash messages.
 *
 * @since 1.0.0
 */
public class FlashMessages extends AbstractModificationTrackingHashMap {

    /**
     * Build a new instance.
     *
     * @since 1.0.0
     */
    public FlashMessages() {
    }

    /**
     * Build a new instance.
     *
     * @param flashMessageData Initial flash messages data
     * @since 1.0.0
     */
    public FlashMessages(final Map<String, String> flashMessageData) {

        super(flashMessageData);
    }
}
