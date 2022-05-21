package com.voidframework.web.http.impl;

import com.voidframework.web.http.Context;
import com.voidframework.web.http.HttpRequest;

public class DefaultContext implements Context {

    private final HttpRequest httpRequest;

    /**
     * Build a new instance.
     *
     * @param httpRequest The HTTP request
     */
    DefaultContext(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public HttpRequest getRequest() {
        return this.httpRequest;
    }
}
