package dev.voidframework.web.server.http;

import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.web.http.WebSocketRequest;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Undertow {@link WebSocketRequest} implementation.
 *
 * @since 1.3.0
 */
public final class UndertowWebSocketRequest implements WebSocketRequest {

    private final WebSocketHttpExchange webSocketHttpExchange;

    private Map<String, List<String>> headerMapCache;
    private Map<String, List<String>> queryStringValuesMapCache;

    /**
     * Build a new instance.
     *
     * @param webSocketHttpExchange Current WebSocket server exchange
     * @since 1.3.0
     */
    public UndertowWebSocketRequest(final WebSocketHttpExchange webSocketHttpExchange) {

        this.webSocketHttpExchange = webSocketHttpExchange;
    }

    @Override
    public boolean acceptContentType(final String contentType) {

        final List<String> acceptHeaderValuesList = this.webSocketHttpExchange.getRequestHeaders().get("Accept");
        if (acceptHeaderValuesList == null || acceptHeaderValuesList.isEmpty()) {
            return false;
        }

        for (final String value : acceptHeaderValuesList.get(0).split(StringConstants.COMMA)) {
            if (value.contains(contentType)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getHeader(final String headerName) {

        final List<String> headerValuesList = this.webSocketHttpExchange.getRequestHeaders().get(headerName);
        if (headerValuesList == null || headerValuesList.isEmpty()) {
            return null;
        }

        return headerValuesList.get(0);
    }

    @Override
    public Map<String, List<String>> getHeaders() {

        if (this.headerMapCache == null) {
            this.headerMapCache = new HashMap<>();
            this.headerMapCache.putAll(this.webSocketHttpExchange.getRequestHeaders());
        }

        return this.headerMapCache;
    }

    @Override
    public String getQueryString() {

        return this.webSocketHttpExchange.getQueryString();
    }

    @Override
    public Map<String, List<String>> getQueryStringParameters() {

        if (this.queryStringValuesMapCache == null) {
            this.queryStringValuesMapCache = this.parseQueryStringParameters();
        }

        return queryStringValuesMapCache;
    }

    @Override
    public String getQueryStringParameter(final String parameterName) {

        if (parameterName == null) {
            return null;
        }

        if (this.queryStringValuesMapCache == null) {
            this.queryStringValuesMapCache = this.parseQueryStringParameters();
        }

        final List<String> queryStringValue = this.queryStringValuesMapCache.get(parameterName);
        if (queryStringValue == null || queryStringValue.isEmpty()) {
            return null;
        }

        return queryStringValue.get(0);
    }

    @Override
    public String getQueryStringParameter(final String parameterName, final String fallbackValue) {

        final String value = this.getQueryStringParameter(parameterName);

        return StringUtils.isBlank(value) ? fallbackValue : value;
    }

    @Override
    public String getRequestURI() {

        return this.webSocketHttpExchange.getRequestURI();
    }

    /**
     * Parses query string parameters.
     *
     * @return Parsed query string parameters
     */
    private Map<String, List<String>> parseQueryStringParameters() {

        final Map<String, List<String>> queryStringValuesMap = new HashMap<>();
        final String[] pairArray = webSocketHttpExchange.getQueryString().split("&");
        for (final String pair : pairArray) {
            final int idx = pair.indexOf("=");

            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8) : pair;
            if (!queryStringValuesMap.containsKey(key)) {
                queryStringValuesMap.put(key, new ArrayList<>());
            }

            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8) : null;
            queryStringValuesMap.get(key).add(value);
        }

        return queryStringValuesMap;
    }
}
