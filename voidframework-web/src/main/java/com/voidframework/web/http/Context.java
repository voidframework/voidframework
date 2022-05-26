package com.voidframework.web.http;

import java.util.Locale;

public final class Context {

    private final HttpRequest httpRequest;

    private Locale locale;

    /**
     * Build a new instance.
     *
     * @param httpRequest The HTTP request
     * @param locale      The current locale
     */
    public Context(final HttpRequest httpRequest, final Locale locale) {
        this.httpRequest = httpRequest;
        this.locale = locale;
    }

    public HttpRequest getRequest() {
        return this.httpRequest;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocal(final Locale locale) {
        this.locale = locale;
    }
}
