package com.voidframework.undertow;

import com.google.common.collect.ImmutableList;
import com.voidframework.core.http.Context;
import com.voidframework.core.routing.HttpMethod;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Undertow context implementation.
 */
public class UndertowContext implements Context {

    private final HttpServerExchange httpServerExchange;

    /**
     * Build a new instance.
     *
     * @param httpServerExchange Current Http server exchange
     */
    public UndertowContext(final HttpServerExchange httpServerExchange) {
        this.httpServerExchange = httpServerExchange;
    }

    @Override
    public String getHeader(final String headerName) {
        return this.httpServerExchange.getRequestHeaders().getFirst(headerName);
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        final Map<String, List<String>> headerMap = new HashMap<>();
        final Iterator<HeaderValues> iterator = this.httpServerExchange.getRequestHeaders().iterator();

        HeaderValues headerValues;
        while (iterator.hasNext()) {
            headerValues = iterator.next();
            headerMap.put(headerValues.getHeaderName().toString(), headerValues.subList(0, headerValues.size()));
        }

        return headerMap;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.valueOf(this.httpServerExchange.getRequestMethod().toString());
    }

    @Override
    public String getQueryString() {
        return this.httpServerExchange.getQueryString();
    }

    @Override
    public String getQueryStringParameter(final String parameterName) {
        if (parameterName == null) {
            return null;
        }

        final Deque<String> parameetersValueDeque = this.httpServerExchange.getQueryParameters().get(parameterName);

        return parameetersValueDeque == null ? null : parameetersValueDeque.getFirst();
    }

    @Override
    public Map<String, List<String>> getQueryStringParameters() {
        final Map<String, List<String>> parametersPerKeyMap = new HashMap<>();

        for (final Map.Entry<String, Deque<String>> entrySet : this.httpServerExchange.getQueryParameters().entrySet()) {
            parametersPerKeyMap.put(entrySet.getKey(), ImmutableList.copyOf(entrySet.getValue()));
        }

        return parametersPerKeyMap;
    }

    @Override
    public String getRemoteHostName() {
        return this.httpServerExchange.getHostName();
    }

    @Override
    public String getRequestURL() {
        return this.httpServerExchange.getRequestURL();
    }

    @Override
    public String getRequestURI() {
        return this.httpServerExchange.getRequestURI();
    }
}
