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

    public static Result badRequest(final String content) {
        return new Result(HttpReturnCode.BAD_REQUEST, content, HttpContentType.TEXT_HTML);
    }

    public static Result badRequest(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.BAD_REQUEST, content, contentType);
    }

    public static Result badRequest(final JsonNode content) {
        return new Result(HttpReturnCode.BAD_REQUEST, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    public static Result created(final String content) {
        return new Result(HttpReturnCode.CREATED, content, HttpContentType.TEXT_HTML);
    }

    public static Result created(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.CREATED, content, contentType);
    }

    public static Result created(final JsonNode content) {
        return new Result(HttpReturnCode.CREATED, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    public static Result forbidden(final String content) {
        return new Result(HttpReturnCode.FORBIDDEN, content, HttpContentType.TEXT_HTML);
    }

    public static Result forbidden(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.FORBIDDEN, content, contentType);
    }

    public static Result forbidden(final JsonNode content) {
        return new Result(HttpReturnCode.FORBIDDEN, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    public static Result internalServerError(final String content) {
        return new Result(HttpReturnCode.INTERNAL_SERVER_ERROR, content, HttpContentType.TEXT_HTML);
    }

    public static Result internalServerError(final JsonNode content) {
        return new Result(HttpReturnCode.INTERNAL_SERVER_ERROR, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    public static Result notFound(final String content) {
        return new Result(HttpReturnCode.NOT_FOUND, content, HttpContentType.TEXT_HTML);
    }

    public static Result notFound(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.NOT_FOUND, content, contentType);
    }

    public static Result notFound(final JsonNode content) {
        return new Result(HttpReturnCode.NOT_FOUND, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    public static Result notImplemented(final String content) {
        return new Result(HttpReturnCode.NOT_IMPLEMENTED, content, HttpContentType.TEXT_HTML);
    }

    public static Result notImplemented(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.NOT_IMPLEMENTED, content, contentType);
    }

    public static Result notImplemented(final JsonNode content) {
        return new Result(HttpReturnCode.NOT_IMPLEMENTED, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    public static Result ok(final String content) {
        return new Result(HttpReturnCode.OK, content, HttpContentType.TEXT_HTML);
    }

    public static Result ok(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.OK, content, contentType);
    }

    public static Result ok(final InputStream content, final String contentType) {
        return new Result(HttpReturnCode.OK, content, contentType);
    }

    public static Result ok(final JsonNode content) {
        return new Result(HttpReturnCode.OK, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    public static Result noContent() {
        return new Result(HttpReturnCode.NO_CONTENT, null, null);
    }

    public static Result redirectTemporaryTo(final String uri) {
        return new Result(HttpReturnCode.FOUND, null, null).withHeader("Location", uri);
    }

    public static Result redirectPermanentlyTo(final String uri) {
        return new Result(HttpReturnCode.MOVED_PERMANENTLY, null, null).withHeader("Location", uri);
    }

    public Result withCookie(final Cookie cookie) {
        this.cookieMap.put(cookie.name(), cookie);
        return this;
    }

    public Result withoutCookie(final Cookie cookie) {
        return withoutCookie(cookie.name());
    }

    public Result withoutCookie(final String cookieName) {
        this.cookieMap.put(cookieName, Cookie.expired(cookieName));
        return this;
    }

    public Result withHeader(final String headerName, final String value) {
        this.headerMap.put(headerName, value);
        return this;
    }

    public Result withoutHeader(final String headerName) {
        this.headerMap.remove(headerName);
        return this;
    }

    public Map<String, Cookie> getCookie() {
        return this.cookieMap;
    }

    public Map<String, String> getHeader() {
        return this.headerMap;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getContentType() {
        return this.contentType;
    }

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
