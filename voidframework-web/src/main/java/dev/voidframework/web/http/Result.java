package dev.voidframework.web.http;

import com.fasterxml.jackson.databind.JsonNode;
import dev.voidframework.core.helper.Json;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * An HTTP result.
 */
public final class Result {

    private final int httpCode;
    private final Object content;
    private final String contentType;
    private final Map<String, String> headerMap;
    private final Map<String, Cookie> cookieMap;

    /**
     * Build a new instance.
     *
     * @param httpCode    The result HTTP code
     * @param content     The result content
     * @param contentType The result content type
     */
    private Result(final int httpCode, final Object content, final String contentType) {
        this.httpCode = httpCode;
        this.content = content;
        this.contentType = contentType;
        this.headerMap = new HashMap<>();
        this.cookieMap = new HashMap<>();
    }

    /**
     * Bad request (400).
     *
     * @param content The content
     * @return A result
     */
    public static Result badRequest(final String content) {
        return new Result(HttpReturnCode.BAD_REQUEST, content, HttpContentType.TEXT_HTML);
    }

    /**
     * Bad request (400).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result badRequest(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.BAD_REQUEST, content, contentType);
    }

    /**
     * Bad request (400).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result badRequest(final JsonNode content) {
        return new Result(HttpReturnCode.BAD_REQUEST, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    /**
     * Created (201).
     *
     * @param content The content
     * @return A result
     */
    public static Result created(final String content) {
        return new Result(HttpReturnCode.CREATED, content, HttpContentType.TEXT_HTML);
    }

    /**
     * Created (201).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result created(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.CREATED, content, contentType);
    }

    /**
     * Created (201).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result created(final JsonNode content) {
        return new Result(HttpReturnCode.CREATED, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    /**
     * Forbidden (403).
     *
     * @param content The content
     * @return A result
     */
    public static Result forbidden(final String content) {
        return new Result(HttpReturnCode.FORBIDDEN, content, HttpContentType.TEXT_HTML);
    }

    /**
     * Forbidden (403).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result forbidden(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.FORBIDDEN, content, contentType);
    }

    /**
     * Forbidden (403).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result forbidden(final JsonNode content) {
        return new Result(HttpReturnCode.FORBIDDEN, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    /**
     * Internal Server Error (500).
     *
     * @param content The content
     * @return A result
     */
    public static Result internalServerError(final String content) {
        return new Result(HttpReturnCode.INTERNAL_SERVER_ERROR, content, HttpContentType.TEXT_HTML);
    }

    /**
     * Internal Server Error (500).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result internalServerError(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.INTERNAL_SERVER_ERROR, content, contentType);
    }

    /**
     * Internal Server Error (500).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result internalServerError(final JsonNode content) {
        return new Result(HttpReturnCode.INTERNAL_SERVER_ERROR, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    /**
     * No Content (204).
     *
     * @return A result
     */
    public static Result noContent() {
        return new Result(HttpReturnCode.NO_CONTENT, null, null);
    }

    /**
     * Not Found (404).
     *
     * @param content The content
     * @return A result
     */
    public static Result notFound(final String content) {
        return new Result(HttpReturnCode.NOT_FOUND, content, HttpContentType.TEXT_HTML);
    }

    /**
     * Not Found (404).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result notFound(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.NOT_FOUND, content, contentType);
    }

    /**
     * Not Found (404).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result notFound(final JsonNode content) {
        return new Result(HttpReturnCode.NOT_FOUND, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    /**
     * Not Implemented (501).
     *
     * @param content The content
     * @return A result
     */
    public static Result notImplemented(final String content) {
        return new Result(HttpReturnCode.NOT_IMPLEMENTED, content, HttpContentType.TEXT_HTML);
    }

    /**
     * Not Implemented (501).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result notImplemented(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.NOT_IMPLEMENTED, content, contentType);
    }

    /**
     * Not Implemented (501).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result notImplemented(final JsonNode content) {
        return new Result(HttpReturnCode.NOT_IMPLEMENTED, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    /**
     * Ok (200).
     *
     * @param content The content
     * @return A result
     */
    public static Result ok(final String content) {
        return new Result(HttpReturnCode.OK, content, HttpContentType.TEXT_HTML);
    }

    /**
     * Ok (200).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result ok(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.OK, content, contentType);
    }

    /**
     * Ok (200).
     *
     * @param content     The input stream content
     * @param contentType The content type
     * @return A result
     */
    public static Result ok(final InputStream content, final String contentType) {
        return new Result(HttpReturnCode.OK, content, contentType);
    }

    /**
     * Ok (200).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result ok(final JsonNode content) {
        return new Result(HttpReturnCode.OK, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    /**
     * Redirect Permanently (301).
     *
     * @param uri The URL to redirect to
     * @return A result
     */
    public static Result redirectPermanentlyTo(final String uri) {
        return new Result(HttpReturnCode.MOVED_PERMANENTLY, null, null).withHeader("Location", uri);
    }

    /**
     * Redirect Temporary (302).
     *
     * @param uri The URL to redirect to
     * @return A result
     */
    public static Result redirectTemporaryTo(final String uri) {
        return new Result(HttpReturnCode.FOUND, null, null).withHeader("Location", uri);
    }

    /**
     * Assigns a new cookie.
     *
     * @param cookie The cookie to assign
     * @return The current result
     */
    public Result withCookie(final Cookie cookie) {
        this.cookieMap.put(cookie.name(), cookie);
        return this;
    }

    /**
     * Removes a cookie.
     *
     * @param cookie The cookie to remove
     * @return The current result
     */
    public Result withoutCookie(final Cookie cookie) {
        return withoutCookie(cookie.name());
    }

    /**
     * Removes a cookie.
     *
     * @param cookieName Name of the cookie to remove
     * @return The current result
     */
    public Result withoutCookie(final String cookieName) {
        this.cookieMap.put(cookieName, Cookie.expired(cookieName));
        return this;
    }

    /**
     * Assigns a new header.
     *
     * @param headerName Name of the header
     * @param value      Value to assign
     * @return The current result
     */
    public Result withHeader(final String headerName, final String value) {
        this.headerMap.put(headerName, value);
        return this;
    }

    /**
     * Removes a header.
     *
     * @param headerName Name of the header to remove
     * @return The current result
     */
    public Result withoutHeader(final String headerName) {
        this.headerMap.remove(headerName);
        return this;
    }

    /**
     * Gets all cookies.
     *
     * @return All cookies
     */
    public Map<String, Cookie> getCookies() {
        return this.cookieMap;
    }

    /**
     * Gets all headers.
     *
     * @return All headers
     */
    public Map<String, String> getHeaders() {
        return this.headerMap;
    }

    /**
     * Gets the HTTP return code.
     *
     * @return The HTTP return code
     */
    public int getHttpCode() {
        return httpCode;
    }

    /**
     * Gets the content type.
     *
     * @return The content type
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * Get the result as InputStream.
     *
     * @return The result as InputStream
     */
    public InputStream getInputStream() {
        if (content == null) {
            return ByteArrayInputStream.nullInputStream();
        } else if (content instanceof byte[]) {
            return new ByteArrayInputStream((byte[]) content);
        } else if (content instanceof InputStream) {
            return (InputStream) content;
        }

        return new ByteArrayInputStream(content.toString().getBytes(StandardCharsets.UTF_8));
    }
}
