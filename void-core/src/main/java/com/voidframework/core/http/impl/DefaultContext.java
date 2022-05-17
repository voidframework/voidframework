package com.voidframework.core.http.impl;

import com.voidframework.core.http.Context;
import com.voidframework.core.http.HttpRequest;

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
