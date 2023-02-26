package dev.voidframework.web.http;

import dev.voidframework.core.lang.TypedMap;

import java.util.Locale;

/**
 * An http request context.
 *
 * @since 1.0.0
 */
public final class Context {

    private final TypedMap attributes;
    private final HttpRequest httpRequest;
    private final Session session;
    private final FlashMessages flashMessages;

    private Locale locale;

    /**
     * Build a new instance.
     *
     * @param httpRequest   The HTTP request
     * @param session       The current session
     * @param flashMessages The current flash message
     * @param locale        The current locale
     * @since 1.0.0
     */
    public Context(final HttpRequest httpRequest,
                   final Session session,
                   final FlashMessages flashMessages,
                   final Locale locale) {

        this.attributes = new TypedMap();
        this.httpRequest = httpRequest;
        this.session = session;
        this.flashMessages = flashMessages;
        this.locale = locale;
    }

    /**
     * Retrieves context attributes.
     *
     * @return The attributes
     * @since 1.0.0
     */
    public TypedMap getAttributes() {

        return this.attributes;
    }

    /**
     * Retrieves the request.
     *
     * @return The current request
     * @since 1.0.0
     */
    public HttpRequest getRequest() {

        return this.httpRequest;
    }

    /**
     * Retrieves the session.
     *
     * @return The current session
     * @since 1.0.0
     */
    public Session getSession() {

        return this.session;
    }

    /**
     * Retrieves the flash messages.
     *
     * @return The flash messages
     * @since 1.0.0
     */
    public FlashMessages getFlashMessages() {

        return this.flashMessages;
    }

    /**
     * Retrieves the locale.
     *
     * @return The current locale
     * @since 1.0.0
     */
    public Locale getLocale() {

        return this.locale;
    }

    /**
     * Sets the locale.
     *
     * @param locale The locale to use
     * @since 1.0.0
     */
    public void setLocal(final Locale locale) {

        this.locale = locale;
    }
}
