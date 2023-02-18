package dev.voidframework.web.http;

import java.time.Duration;

/**
 * A Cookie.
 *
 * @param name         The cookie name
 * @param value        The cookie value
 * @param domain       The cookie domain
 * @param path         The cookie path
 * @param isHttpOnly   Is the cookie only be accessed via HTTP?
 * @param isSecure     Is the cookie secured? If true, sent only for HTTPS requests
 * @param sameSiteMode The same site mode (None, Lax, or Strict)
 * @param timeToLive   The cookie time to live
 * @since 1.0.0
 */
public record Cookie(String name,
                     String value,
                     String domain,
                     String path,
                     boolean isHttpOnly,
                     boolean isSecure,
                     String sameSiteMode,
                     Duration timeToLive) {

    private static final String DEFAULT_PATH = "/";
    private static final String DEFAULT_SAME_SITE_POLICY = "Lax";

    /**
     * Build an expired Cookie.
     *
     * @param name The cookie name
     * @return Newly created expired cookie
     * @since 1.0.0
     */
    static Cookie expired(final String name) {

        return new Cookie(name, null, null, DEFAULT_PATH, false, false, DEFAULT_SAME_SITE_POLICY, Duration.ZERO);
    }

    /**
     * Build a new Cookie.
     *
     * @param name  The cookie name
     * @param value The cookie value
     * @return Newly created cookie
     * @since 1.0.0
     */
    public static Cookie of(final String name, final String value) {

        return new Cookie(name, value, null, DEFAULT_PATH, false, false, DEFAULT_SAME_SITE_POLICY, null);
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
     * @since 1.0.0
     */
    public static Cookie of(final String name, final String value, final boolean isHttpOnly, final boolean isSecure, final Duration timeToLive) {

        return new Cookie(name, value, null, DEFAULT_PATH, isHttpOnly, isSecure, DEFAULT_SAME_SITE_POLICY, timeToLive);
    }

    /**
     * Build a new Cookie.
     *
     * @param name         The cookie name
     * @param value        The cookie value
     * @param isHttpOnly   Is the cookie only be accessed via HTTP? isSecure
     * @param isSecure     Is the cookie secured? If true, sent only for HTTPS
     * @param sameSiteMode The same site mode (None, Lax, or Strict)
     * @param timeToLive   The cookie time to live
     * @return Newly created cookie
     * @since 1.0.1
     */
    public static Cookie of(final String name,
                            final String value,
                            final boolean isHttpOnly,
                            final boolean isSecure,
                            final String sameSiteMode,
                            final Duration timeToLive) {

        return new Cookie(name, value, null, DEFAULT_PATH, isHttpOnly, isSecure, sameSiteMode, timeToLive);
    }

    /**
     * Build a new Cookie.
     *
     * @param name       The cookie name
     * @param value      The cookie value
     * @param timeToLive The cookie time to live
     * @return Newly created cookie
     * @since 1.0.0
     */
    public static Cookie of(final String name, final String value, final Duration timeToLive) {

        return new Cookie(name, value, null, DEFAULT_PATH, false, false, DEFAULT_SAME_SITE_POLICY, timeToLive);
    }
}
