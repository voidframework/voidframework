package com.voidframework.core.http;

import com.voidframework.core.routing.HttpMethod;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * An HTTP request.
 */
public interface HttpRequest {

    /**
     * Return the request charset.
     *
     * @return The request charset
     */
    String getCharset();

    /**
     * Return a single header.
     *
     * @param headerName The header name
     * @return The requested header value, otherwise, a null value
     */
    String getHeader(final String headerName);

    /**
     * Return all headers.
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
     * Return the query string, without the leading "?".
     *
     * @return The query string
     */
    String getQueryString();

    /**
     * Return the query string parameter value.
     *
     * @param parameterName The parameter name
     * @return The query string parameter value
     */
    String getQueryStringParameter(String parameterName);

    /**
     * Return all query string parameters.
     *
     * @return All query string parameters
     */
    Map<String, List<String>> getQueryStringParameters();

    /**
     * Return the remote host that this request was sent to.
     *
     * @return The remote host name
     */
    String getRemoteHostName();

    /**
     * Return the complete URL as seen by the user.
     *
     * @return The request URL
     */
    String getRequestURL();

    /**
     * Return the original request URI. This will include the host name, protocol etc if it was specified.
     * This is not decoded in any way, and does not include the query string.
     *
     * @return The request URI
     */
    String getRequestURI();

    byte[] getBodyContent();
}
