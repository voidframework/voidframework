package dev.voidframework.web.http;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * An HTTP request.
 *
 * @since 1.0.0
 */
public interface HttpRequest {

    /**
     * Returns the request charset.
     *
     * @return The request charset
     * @since 1.0.0
     */
    String getCharset();

    /**
     * Returns a cookie.
     *
     * @param cookieName The cookie name
     * @return The cookie, otherwise, null
     * @since 1.0.0
     */
    Cookie getCookie(final String cookieName);

    /**
     * Checks if the current request accept a specific content type.
     *
     * @param contentType The content type
     * @return {@code true} if the content type is accepted, otherwise, {@code false}
     * @since 1.1.0
     */
    boolean acceptContentType(final String contentType);

    /**
     * Returns a single header.
     *
     * @param headerName The header name
     * @return The requested header value, otherwise, a null value
     * @since 1.0.0
     */
    String getHeader(final String headerName);

    /**
     * Returns all headers.
     *
     * @return All headers
     * @since 1.0.0
     */
    Map<String, List<String>> getHeaders();

    /**
     * Get the HTTP request method.
     *
     * @return The request method
     * @since 1.0.0
     */
    HttpMethod getHttpMethod();

    /**
     * Get the input stream to read the request. Can only be called one time.
     *
     * @return The input stream
     * @since 1.0.0
     */
    InputStream getInputSteam();

    /**
     * Returns the query string, without the leading "?".
     *
     * @return The query string
     * @since 1.0.0
     */
    String getQueryString();

    /**
     * Returns the query string parameter value.
     * If the parameter does not exist, null value will be returned.
     *
     * @param parameterName The parameter name
     * @return The query string parameter value
     * @since 1.0.0
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
     * Returns the query string parameter values as list.
     * If the parameter does not exist, an empty list will be returned.
     *
     * @param parameterName The parameter name
     * @return The query string parameter values as list
     * @since 1.3.0
     */
    List<String> getQueryStringParameterAsList(final String parameterName);

    /**
     * Returns the query string parameter values as list.
     * If the parameter does not exist or associated value is empty, fallback value will be returned.
     *
     * @param parameterName The parameter name
     * @param fallbackValue The value to use if the parameter does not exist or the value is empty
     * @return The query string parameter values as list
     * @since 1.6.0
     */
    List<String> getQueryStringParameterAsList(final String parameterName, final List<String> fallbackValue);

    /**
     * Returns all query string parameters.
     *
     * @return All query string parameters
     * @since 1.0.0
     */
    Map<String, List<String>> getQueryStringParameters();

    /**
     * Returns the remote host that this request was sent to.
     *
     * @return The remote host name
     * @since 1.0.0
     */
    String getRemoteHostName();

    /**
     * Returns the complete URL as seen by the user.
     *
     * @return The request URL
     * @since 1.0.0
     */
    String getRequestURL();

    /**
     * Returns the original request URI.
     * This is not decoded in any way, and does not include the query string.
     *
     * @return The request URI
     * @since 1.0.0
     */
    String getRequestURI();

    /**
     * Returns the body content;
     *
     * @return The body content
     * @since 1.0.0
     */
    HttpRequestBodyContent getBodyContent();
}
