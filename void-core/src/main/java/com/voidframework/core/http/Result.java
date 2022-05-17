package com.voidframework.core.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.voidframework.core.helper.Json;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * An HTTP result.
 */
public final class Result {

    private int httpCode;
    private Object content;
    private String contentType;
    private Map<String, String> headerMap = new HashMap<>();

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

    public static Result ok(final JsonNode content) {
        return new Result(HttpReturnCode.OK, Json.toString(content), HttpContentType.APPLICATION_JSON);
    }

    public static Result noContent() {
        return new Result(HttpReturnCode.NO_CONTENT, null, null);
    }

    public static Result redirectTemporaryTo(final String uri) {
        return new Result(HttpReturnCode.FOUND, null, null).setHeader("Location", uri);
    }

    public static Result redirectPermanentlyTo(final String uri) {
        return new Result(HttpReturnCode.MOVED_PERMANENTLY, null, null).setHeader("Location", uri);
    }

    public InputStream getInputStream() {
        if (content == null) {
            return ByteArrayInputStream.nullInputStream();
        } else if (content instanceof byte[]) {
            return new ByteArrayInputStream((byte[]) content);
        }

        return new ByteArrayInputStream(content.toString().getBytes(StandardCharsets.UTF_8));
    }

    public Result setHeader(final String headerName, final String value) {
        this.headerMap.put(headerName, value);
        return this;
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
}
