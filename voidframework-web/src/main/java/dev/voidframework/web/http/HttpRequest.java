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
     *
     * @param parameterName The parameter name
     * @return The query string parameter value
     */
    String getQueryStringParameter(final String parameterName);

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
