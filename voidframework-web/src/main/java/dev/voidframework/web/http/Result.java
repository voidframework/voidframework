package dev.voidframework.web.http;

import com.fasterxml.jackson.databind.JsonNode;
import dev.voidframework.core.utils.JsonUtils;
import dev.voidframework.web.http.resultprocessor.NoContentResultProcessor;
import dev.voidframework.web.http.resultprocessor.ObjectResultProcessor;
import dev.voidframework.web.http.resultprocessor.ResultProcessor;
import dev.voidframework.web.http.resultprocessor.TemplateResultProcessor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * An HTTP result.
 */
public final class Result {

    private static final NoContentResultProcessor NO_CONTENT_RESULT_PROCESSOR = new NoContentResultProcessor();
    private static final String NO_CONTENT_TYPE = null;

    private final int httpCode;
    private final String contentType;
    private final ResultProcessor resultProcessor;
    private final Map<String, String> headerMap;
    private final Map<String, Cookie> cookieMap;

    /**
     * Build an empty new instance.
     * This constructor is useful during deserialize process
     */
    @SuppressWarnings("unused")
    public Result() {

        this.httpCode = -1;
        this.resultProcessor = null;
        this.contentType = null;
        this.headerMap = Map.of();
        this.cookieMap = Map.of();
    }

    /**
     * Build a new instance.
     *
     * @param httpCode        The result HTTP code
     * @param resultProcessor The result processor
     * @param contentType     The result content type
     */
    private Result(final int httpCode, final ResultProcessor resultProcessor, final String contentType) {

        this.httpCode = httpCode;
        this.resultProcessor = resultProcessor;
        this.contentType = contentType;
        this.headerMap = new HashMap<>();
        this.cookieMap = new HashMap<>();
    }

    /**
     * Bad request (400).
     *
     * @return A result
     */
    public static Result badRequest() {

        return new Result(
            HttpReturnCode.BAD_REQUEST,
            NO_CONTENT_RESULT_PROCESSOR,
            HttpContentType.TEXT_HTML);
    }

    /**
     * Bad request (400).
     *
     * @param content The content
     * @return A result
     */
    public static Result badRequest(final String content) {

        return new Result(
            HttpReturnCode.BAD_REQUEST,
            new ObjectResultProcessor(content),
            HttpContentType.TEXT_HTML);
    }

    /**
     * Bad request (400).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result badRequest(final byte[] content, final String contentType) {

        return new Result(
            HttpReturnCode.BAD_REQUEST,
            new ObjectResultProcessor(content),
            contentType);
    }

    /**
     * Bad request (400).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result badRequest(final JsonNode content) {

        return new Result(
            HttpReturnCode.BAD_REQUEST,
            new ObjectResultProcessor(JsonUtils.toString(content)),
            HttpContentType.APPLICATION_JSON);
    }

    /**
     * Bad request (400).
     *
     * @param templateResult The template to render
     * @return A result
     */
    public static Result badRequest(final TemplateResult templateResult) {

        return new Result(
            HttpReturnCode.BAD_REQUEST,
            new TemplateResultProcessor(templateResult.templateName, templateResult.dataModel),
            HttpContentType.TEXT_HTML);
    }

    /**
     * Created (201).
     *
     * @return A result
     */
    public static Result created() {

        return new Result(
            HttpReturnCode.CREATED,
            NO_CONTENT_RESULT_PROCESSOR,
            HttpContentType.TEXT_HTML);
    }

    /**
     * Created (201).
     *
     * @param content The content
     * @return A result
     */
    public static Result created(final String content) {

        return new Result(
            HttpReturnCode.CREATED,
            new ObjectResultProcessor(content),
            HttpContentType.TEXT_HTML);
    }

    /**
     * Created (201).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result created(final byte[] content, final String contentType) {

        return new Result(
            HttpReturnCode.CREATED,
            new ObjectResultProcessor(content),
            contentType);
    }

    /**
     * Created (201).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result created(final JsonNode content) {

        return new Result(
            HttpReturnCode.CREATED,
            new ObjectResultProcessor(JsonUtils.toString(content)),
            HttpContentType.APPLICATION_JSON);
    }

    /**
     * Created (201).
     *
     * @param templateResult The template to render
     * @return A result
     */
    public static Result created(final TemplateResult templateResult) {

        return new Result(
            HttpReturnCode.CREATED,
            new TemplateResultProcessor(templateResult.templateName, templateResult.dataModel),
            HttpContentType.TEXT_HTML);
    }

    /**
     * Forbidden (403).
     *
     * @return A result
     */
    public static Result forbidden() {

        return new Result(
            HttpReturnCode.FORBIDDEN,
            NO_CONTENT_RESULT_PROCESSOR,
            HttpContentType.TEXT_HTML);
    }

    /**
     * Forbidden (403).
     *
     * @param content The content
     * @return A result
     */
    public static Result forbidden(final String content) {

        return new Result(
            HttpReturnCode.FORBIDDEN,
            new ObjectResultProcessor(content),
            HttpContentType.TEXT_HTML);
    }

    /**
     * Forbidden (403).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result forbidden(final byte[] content, final String contentType) {

        return new Result(
            HttpReturnCode.FORBIDDEN,
            new ObjectResultProcessor(content),
            contentType);
    }

    /**
     * Forbidden (403).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result forbidden(final JsonNode content) {

        return new Result(
            HttpReturnCode.FORBIDDEN,
            new ObjectResultProcessor(JsonUtils.toString(content)),
            HttpContentType.APPLICATION_JSON);
    }

    /**
     * Forbidden (403).
     *
     * @param templateResult The template to render
     * @return A result
     */
    public static Result forbidden(final TemplateResult templateResult) {

        return new Result(
            HttpReturnCode.FORBIDDEN,
            new TemplateResultProcessor(templateResult.templateName, templateResult.dataModel),
            HttpContentType.TEXT_HTML);
    }

    /**
     * Internal Server Error (500).
     *
     * @return A result
     */
    public static Result internalServerError() {

        return new Result(
            HttpReturnCode.INTERNAL_SERVER_ERROR,
            NO_CONTENT_RESULT_PROCESSOR,
            HttpContentType.TEXT_HTML);
    }

    /**
     * Internal Server Error (500).
     *
     * @param content The content
     * @return A result
     */
    public static Result internalServerError(final String content) {

        return new Result(
            HttpReturnCode.INTERNAL_SERVER_ERROR,
            new ObjectResultProcessor(content),
            HttpContentType.TEXT_HTML);
    }

    /**
     * Internal Server Error (500).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result internalServerError(final byte[] content, final String contentType) {

        return new Result(
            HttpReturnCode.INTERNAL_SERVER_ERROR,
            new ObjectResultProcessor(content),
            contentType);
    }

    /**
     * Internal Server Error (500).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result internalServerError(final JsonNode content) {

        return new Result(
            HttpReturnCode.INTERNAL_SERVER_ERROR,
            new ObjectResultProcessor(JsonUtils.toString(content)),
            HttpContentType.APPLICATION_JSON);
    }

    /**
     * Internal Server Error (500).
     *
     * @param templateResult The template to render
     * @return A result
     */
    public static Result internalServerError(final TemplateResult templateResult) {

        return new Result(
            HttpReturnCode.NOT_FOUND,
            new TemplateResultProcessor(templateResult.templateName, templateResult.dataModel),
            HttpContentType.TEXT_HTML);
    }

    /**
     * No Content (204).
     *
     * @return A result
     */
    public static Result noContent() {

        return new Result(
            HttpReturnCode.NO_CONTENT,
            NO_CONTENT_RESULT_PROCESSOR,
            NO_CONTENT_TYPE);
    }

    /**
     * Not Found (404).
     *
     * @return A result
     */
    public static Result notFound() {

        return new Result(
            HttpReturnCode.NOT_FOUND,
            NO_CONTENT_RESULT_PROCESSOR,
            HttpContentType.TEXT_HTML);
    }

    /**
     * Not Found (404).
     *
     * @param content The content
     * @return A result
     */
    public static Result notFound(final String content) {

        return new Result(
            HttpReturnCode.NOT_FOUND,
            new ObjectResultProcessor(content),
            HttpContentType.TEXT_HTML);
    }

    /**
     * Not Found (404).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result notFound(final byte[] content, final String contentType) {

        return new Result(
            HttpReturnCode.NOT_FOUND,
            new ObjectResultProcessor(content),
            contentType);
    }

    /**
     * Not Found (404).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result notFound(final JsonNode content) {

        return new Result(
            HttpReturnCode.NOT_FOUND,
            new ObjectResultProcessor(JsonUtils.toString(content)),
            HttpContentType.APPLICATION_JSON);
    }

    /**
     * Not Found (404).
     *
     * @param templateResult The template to render
     * @return A result
     */
    public static Result notFound(final TemplateResult templateResult) {

        return new Result(
            HttpReturnCode.NOT_FOUND,
            new TemplateResultProcessor(templateResult.templateName, templateResult.dataModel),
            HttpContentType.TEXT_HTML);
    }

    /**
     * Not Implemented (501).
     *
     * @return A result
     */
    public static Result notImplemented() {

        return new Result(
            HttpReturnCode.NOT_IMPLEMENTED,
            NO_CONTENT_RESULT_PROCESSOR,
            HttpContentType.TEXT_HTML);
    }

    /**
     * Not Implemented (501).
     *
     * @param content The content
     * @return A result
     */
    public static Result notImplemented(final String content) {

        return new Result(
            HttpReturnCode.NOT_IMPLEMENTED,
            new ObjectResultProcessor(content),
            HttpContentType.TEXT_HTML);
    }

    /**
     * Not Implemented (501).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result notImplemented(final byte[] content, final String contentType) {

        return new Result(
            HttpReturnCode.NOT_IMPLEMENTED,
            new ObjectResultProcessor(content),
            contentType);
    }

    /**
     * Not Implemented (501).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result notImplemented(final JsonNode content) {

        return new Result(
            HttpReturnCode.NOT_IMPLEMENTED,
            new ObjectResultProcessor(JsonUtils.toString(content)),
            HttpContentType.APPLICATION_JSON);
    }

    /**
     * Not Implemented (501).
     *
     * @param templateResult The template to render
     * @return A result
     */
    public static Result notImplemented(final TemplateResult templateResult) {

        return new Result(
            HttpReturnCode.NOT_IMPLEMENTED,
            new TemplateResultProcessor(templateResult.templateName, templateResult.dataModel),
            HttpContentType.TEXT_HTML);
    }

    /**
     * Ok (200).
     *
     * @return A result
     */
    public static Result ok() {

        return new Result(
            HttpReturnCode.OK,
            NO_CONTENT_RESULT_PROCESSOR,
            HttpContentType.TEXT_HTML);
    }

    /**
     * Ok (200).
     *
     * @param content The content
     * @return A result
     */
    public static Result ok(final String content) {

        return new Result(
            HttpReturnCode.OK,
            new ObjectResultProcessor(content),
            HttpContentType.TEXT_HTML);
    }

    /**
     * Ok (200).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result ok(final byte[] content, final String contentType) {

        return new Result(
            HttpReturnCode.OK,
            new ObjectResultProcessor(content),
            contentType);
    }

    /**
     * Ok (200).
     *
     * @param content     The input stream content
     * @param contentType The content type
     * @return A result
     */
    public static Result ok(final InputStream content, final String contentType) {

        return new Result(
            HttpReturnCode.OK,
            new ObjectResultProcessor(content),
            contentType);
    }

    /**
     * Ok (200).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result ok(final JsonNode content) {

        return new Result(
            HttpReturnCode.OK,
            new ObjectResultProcessor(JsonUtils.toString(content)),
            HttpContentType.APPLICATION_JSON);
    }

    /**
     * Ok (200).
     *
     * @param templateResult The template to render
     * @return A result
     */
    public static Result ok(final TemplateResult templateResult) {

        return new Result(
            HttpReturnCode.OK,
            new TemplateResultProcessor(templateResult.templateName, templateResult.dataModel),
            HttpContentType.TEXT_HTML);
    }

    /**
     * Redirect Permanently (301).
     *
     * @param uri The URL to redirect to
     * @return A result
     */
    public static Result redirectPermanentlyTo(final String uri) {

        return new Result(
            HttpReturnCode.MOVED_PERMANENTLY,
            NO_CONTENT_RESULT_PROCESSOR,
            NO_CONTENT_TYPE
        ).withHeader("Location", uri);
    }

    /**
     * Redirect Temporary (302).
     *
     * @param uri The URL to redirect to
     * @return A result
     */
    public static Result redirectTemporaryTo(final String uri) {

        return new Result(
            HttpReturnCode.FOUND,
            NO_CONTENT_RESULT_PROCESSOR,
            NO_CONTENT_TYPE
        ).withHeader("Location", uri);
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
     * Assigns a multiple headers.
     *
     * @param headerMap headers to assign
     * @return The current result
     */
    public Result withHeaders(final Map<String, String> headerMap) {

        this.headerMap.putAll(headerMap);
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
     * Get tje result processor.
     *
     * @return The result processor
     */
    public ResultProcessor getResultProcessor() {

        return this.resultProcessor;
    }
}
