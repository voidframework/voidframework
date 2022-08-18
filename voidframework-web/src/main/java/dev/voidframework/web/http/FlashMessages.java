package dev.voidframework.web.http;

import java.util.Map;

/**
 * HTTP Flash messages.
 */
public class FlashMessages extends AbstractModificationTrackingHashMap {

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
}
