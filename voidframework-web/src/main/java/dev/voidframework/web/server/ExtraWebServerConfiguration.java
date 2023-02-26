package dev.voidframework.web.server;

import io.undertow.Undertow;

/**
 * Allows application of a custom configuration to the Undertow web server.
 *
 * @since 1.3.0
 */
public interface ExtraWebServerConfiguration {

    /**
     * Applies a custom configuration to the Undertow web server.
     *
     * @param undertowBuilder The current Undertow web server builder
     * @since 1.3.0
     */
    void doExtraConfiguration(final Undertow.Builder undertowBuilder);
}
