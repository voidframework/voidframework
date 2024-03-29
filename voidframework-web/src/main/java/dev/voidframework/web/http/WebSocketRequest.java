package dev.voidframework.web.http;

import java.util.List;
import java.util.Map;

/**
 * A WebSocket request.
 *
 * @since 1.3.0
 */
public interface WebSocketRequest {

    /**
     * Checks if the current request accept a specific content type.
     *
     * @param contentType The content type
     * @return {@code true} if the content type is accepted, otherwise, {@code false}
     * @since 1.3.0
     */
    boolean acceptContentType(final String contentType);

    /**
     * Returns a single header.
     *
     * @param headerName The header name
     * @return The requested header value, otherwise, a null value
     * @since 1.3.0
     */
    String getHeader(final String headerName);

    /**
     * Returns all headers.
     *
     * @return All headers
     * @since 1.3.0
     */
    Map<String, List<String>> getHeaders();

    /**
     * Returns the query string, without the leading "?".
     *
     * @return The query string
     * @since 1.3.0
     */
    String getQueryString();

    /**
     * Returns all query string parameters.
     *
     * @return All query string parameters
     * @since 1.3.0
     */
    Map<String, List<String>> getQueryStringParameters();

    /**
     * Returns the query string parameter value.
     * If the parameter does not exist, null value will be returned.
     *
     * @param parameterName The parameter name
     * @return The query string parameter value
     * @since 1.3.0
     */
    String getQueryStringParameter(final String parameterName);

    /**
     * Returns the query string parameter value.
     * If the parameter does not exist or associated value is blank, fallback value will be returned.
     *
     * @param parameterName The parameter name
     * @param fallbackValue The value to use if the parameter does not exist or associated value is blank
     * @return The query string parameter value
     * @since 1.6.0
     */
    String getQueryStringParameter(final String parameterName, final String fallbackValue);

    /**
     * Returns the original request URI.
     * This is not decoded in any way, and does not include the query string.
     *
     * @return The request URI
     * @since 1.3.0
     */
    String getRequestURI();
}
