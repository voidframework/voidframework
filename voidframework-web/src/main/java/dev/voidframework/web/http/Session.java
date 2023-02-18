package dev.voidframework.web.http;

import java.util.Map;

/**
 * An HTTP session.
 *
 * @since 1.0.0
 */
public class Session extends AbstractModificationTrackingHashMap {

    /**
     * Build a new instance.
     *
     * @since 1.0.0
     */
    public Session() {
    }

    /**
     * Build a new instance.
     *
     * @param sesionData Initial session data
     * @since 1.0.0
     */
    public Session(final Map<String, String> sesionData) {

        super(sesionData);
    }
}
