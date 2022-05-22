package com.voidframework.web.server;

import com.google.common.collect.ImmutableList;
import com.voidframework.web.http.HttpRequest;
import com.voidframework.web.http.HttpRequestBodyContent;
import com.voidframework.web.routing.HttpMethod;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;

import java.io.InputStream;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Undertow {@link HttpRequest} implementation.
 */
public final class UndertowRequest implements HttpRequest {

    private final HttpServerExchange httpServerExchange;
    private final HttpRequestBodyContent httpRequestBodyContent;

    /**
     * Build a new instance.
     *
     * @param httpServerExchange     Current Http server exchange
     * @param httpRequestBodyContent Current Http request body content
     */
    public UndertowRequest(final HttpServerExchange httpServerExchange,
                           final HttpRequestBodyContent httpRequestBodyContent) {
        this.httpServerExchange = httpServerExchange;
        this.httpRequestBodyContent = httpRequestBodyContent;
    }

    @Override
    public String getCharset() {
        return httpServerExchange.getRequestCharset();
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
    public InputStream getInputSteam() {
        return httpServerExchange.getInputStream();
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

    @Override
    public HttpRequestBodyContent getBodyContent() {
        return this.httpRequestBodyContent;
    }
}
