package dev.voidframework.web.http;

import java.util.Map;

/**
 * An HTTP session.
 */
public class Session extends AbstractModificationTrackingHashMap {

    /**
     * Build a new instance.
     */
    public Session() {
    }

    /**
     * Build a new instance.
     *
     * @param sesionData Initial session data
     */
    public Session(final Map<String, String> sesionData) {

        super(sesionData);
    }
}
