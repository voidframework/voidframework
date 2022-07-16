package dev.voidframework.web.http;

import java.time.Duration;

/**
 * A Cookie.
 *
 * @param name       The cookie name
 * @param value      The cookie value
 * @param domain     The cookie domain
 * @param path       The cookie path
 * @param isHttpOnly Is the cookie only be accessed via HTTP?
 * @param isSecure   Is the cookie secured? If true, sent only for HTTPS requests
 * @param timeToLive The cookie time to live
 */
public record Cookie(String name,
                     String value,
                     String domain,
                     String path,
                     boolean isHttpOnly,
                     boolean isSecure,
                     Duration timeToLive) {

    /**
     * Build an expired Cookie.
     *
     * @param name The cookie name
     * @return Newly created expired cookie
     */
    static Cookie expired(final String name) {

        return new Cookie(name, null, null, "/", false, false, Duration.ZERO);
    }

    /**
     * Build a new Cookie.
     *
     * @param name  The cookie name
     * @param value The cookie value
     * @return Newly created cookie
     */
    public static Cookie of(final String name, final String value) {

        return new Cookie(name, value, null, "/", false, false, null);
    }

    /**
     * Build a new Cookie.
     *
     * @param name       The cookie name
     * @param value      The cookie value
     * @param isHttpOnly Is the cookie only be accessed via HTTP? isSecure
     * @param isSecure   Is the cookie secured? If true, sent only for HTTPS
     * @param timeToLive The cookie time to live
     * @return Newly created cookie
     */
    public static Cookie of(final String name, final String value, final boolean isHttpOnly, final boolean isSecure, final Duration timeToLive) {

        return new Cookie(name, value, null, "/", isHttpOnly, isSecure, timeToLive);
    }

    /**
     * Build a new Cookie.
     *
     * @param name       The cookie name
     * @param value      The cookie value
     * @param timeToLive The cookie time to live
     * @return Newly created cookie
     */
    public static Cookie of(final String name, final String value, final Duration timeToLive) {

        return new Cookie(name, value, null, "/", false, false, timeToLive);
    }
}
