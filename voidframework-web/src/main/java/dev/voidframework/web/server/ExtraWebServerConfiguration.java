package dev.voidframework.web.server;

import io.undertow.Undertow;

/**
 * Allows application of a custom configuration to the Undertow web server.
 */
public interface ExtraWebServerConfiguration {

    /**
     * Applies a custom configuration to the Undertow web server.
     *
     * @param undertowBuilder The current Undertow web server builder
     */
    void doExtraConfiguration(final Undertow.Builder undertowBuilder);
}
