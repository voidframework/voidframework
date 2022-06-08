package dev.voidframework.web.http;

import java.util.Locale;

/**
 * An http request context.
 */
public final class Context {

    private final HttpRequest httpRequest;

    private final Session session;
    private Locale locale;

    /**
     * Build a new instance.
     *
     * @param httpRequest The HTTP request
     * @param session     The current session
     * @param locale      The current locale
     */
    public Context(final HttpRequest httpRequest, final Session session, final Locale locale) {
        this.httpRequest = httpRequest;
        this.session = session;
        this.locale = locale;
    }

    /**
     * Retrieves the request.
     *
     * @return The current request
     */
    public HttpRequest getRequest() {
        return this.httpRequest;
    }

    /**
     * Retrieves the session.
     *
     * @return The current session
     */
    public Session getSession() {
        return this.session;
    }

    /**
     * Retrieves the locale.
     *
     * @return The current locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the locale.
     *
     * @param locale The locale to use
     */
    public void setLocal(final Locale locale) {
        this.locale = locale;
    }
}
