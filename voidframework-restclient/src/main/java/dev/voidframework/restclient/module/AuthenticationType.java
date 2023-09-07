package dev.voidframework.restclient.module;

/**
 * All available authentication types.
 *
 * @since 1.10.0
 */
public enum AuthenticationType {

    /**
     * API key authentication.
     *
     * @since 1.10.0
     */
    API_KEY,

    /**
     * Basic authentication.
     *
     * @since 1.10.0
     */
    BASIC,

    /**
     * Bearer authentication (also called token authentication).
     *
     * @since 1.10.0
     */
    BEARER
}
