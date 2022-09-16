package dev.voidframework.web.http;

import java.util.List;
import java.util.Map;

/**
 * A WebSocket request.
 */
public interface WebSocketRequest {

    /**
     * Checks if the current request accept a specific content type.
     *
     * @param contentType The content type
     * @return {@code true} if the content type is accepted, otherwise, {@code false}
     */
    boolean acceptContentType(final String contentType);

    /**
     * Returns a single header.
     *
     * @param headerName The header name
     * @return The requested header value, otherwise, a null value
     */
    String getHeader(final String headerName);

    /**
     * Returns all headers.
     *
     * @return All headers
     */
    Map<String, List<String>> getHeaders();

    /**
     * Returns the query string, without the leading "?".
     *
     * @return The query string
     */
    String getQueryString();

    /**
     * Returns all query string parameters.
     *
     * @return All query string parameters
     */
    Map<String, List<String>> getQueryStringParameters();

    /**
     * Returns the query string parameter value.
     *
     * @param parameterName The parameter name
     * @return The query string parameter value
     */
    String getQueryStringParameter(final String parameterName);

    /**
     * Returns the original request URI.
     * This is not decoded in any way, and does not include the query string.
     *
     * @return The request URI
     */
    String getRequestURI();
}
