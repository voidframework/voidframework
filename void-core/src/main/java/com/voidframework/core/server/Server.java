package com.voidframework.core.server;

import java.util.List;

/**
 * An HTTP server.
 */
public interface Server {

    /**
     * Start the server.
     *
     * @return The listener(s) information (ie: http://127.0.0.1:8080)
     */
    List<ListenerInformation> start();

    /**
     * Stop the server.
     */
    void onStop();
}
