package dev.voidframework.web.http;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * An HTTP request.
 */
public interface HttpRequest {

    /**
     * Returns the request charset.
     *
     * @return The request charset
     */
    String getCharset();

    /**
     * Returns a cookie.
     *
     * @param cookieName The cookie name
     * @return The cookie, otherwise, null
     */
    Cookie getCookie(final String cookieName);

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
     * Get the HTTP request method.
     *
     * @return The request method
     */
    HttpMethod getHttpMethod();

    /**
     * Get the input stream to read the request. Can only be called one time.
     *
     * @return The input stream
     */
    InputStream getInputSteam();

    /**
     * Returns the query string, without the leading "?".
     *
     * @return The query string
     */
    String getQueryString();

    /**
     * Returns the query string parameter value.
     * If the parameter does not exist, null value will be returned.
     *
     * @param parameterName The parameter name
     * @return The query string parameter value
     */
    String getQueryStringParameter(final String parameterName);

    /**
     * Returns the query string parameter value.
     * If the parameter does not exist or associated value is blank, fallback value will be returned.
     *
     * @param parameterName The parameter name
     * @param fallbackValue The value to use if the parameter does not exist or associated value is blank
     * @return The query string parameter value
     */
    String getQueryStringParameter(final String parameterName, final String fallbackValue);

    /**
     * Returns the query string parameter values as list.
     * If the parameter does not exist, an empty list will be returned.
     *
     * @param parameterName The parameter name
     * @return The query string parameter values as list
     */
    List<String> getQueryStringParameterAsList(final String parameterName);

    /**
     * Returns the query string parameter values as list.
     * If the parameter does not exist or associated value is empty, fallback value will be returned.
     *
     * @param parameterName The parameter name
     * @param fallbackValue The value to use if the parameter does not exist or the value is empty
     * @return The query string parameter values as list
     */
    List<String> getQueryStringParameterAsList(final String parameterName, final List<String> fallbackValue);

    /**
     * Returns all query string parameters.
     *
     * @return All query string parameters
     */
    Map<String, List<String>> getQueryStringParameters();

    /**
     * Returns the remote host that this request was sent to.
     *
     * @return The remote host name
     */
    String getRemoteHostName();

    /**
     * Returns the complete URL as seen by the user.
     *
     * @return The request URL
     */
    String getRequestURL();

    /**
     * Returns the original request URI.
     * This is not decoded in any way, and does not include the query string.
     *
     * @return The request URI
     */
    String getRequestURI();

    /**
     * Returns the body content;
     *
     * @return The body content
     */
    HttpRequestBodyContent getBodyContent();
}
