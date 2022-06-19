package dev.voidframework.web.http;

import com.fasterxml.jackson.databind.JsonNode;
import dev.voidframework.core.helper.Json;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.template.exception.TemplateException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * An HTTP result.
 */
public final class Result {

    private static final NoContentResultProcessor NO_CONTENT_RESULT_PROCESSOR = new NoContentResultProcessor();

    private final int httpCode;
    private final String contentType;
    private final ResultProcessor resultProcessor;
    private final Map<String, String> headerMap;
    private final Map<String, Cookie> cookieMap;

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
     * @param content The content
     * @return A result
     */
    public static Result badRequest(final String content) {
        return new Result(HttpReturnCode.BAD_REQUEST, new ObjectResultProcessor(content), HttpContentType.TEXT_HTML);
    }

    /**
     * Bad request (400).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result badRequest(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.BAD_REQUEST, new ObjectResultProcessor(content), contentType);
    }

    /**
     * Bad request (400).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result badRequest(final JsonNode content) {
        return new Result(HttpReturnCode.BAD_REQUEST, new ObjectResultProcessor(Json.toString(content)), HttpContentType.APPLICATION_JSON);
    }

    /**
     * Created (201).
     *
     * @param content The content
     * @return A result
     */
    public static Result created(final String content) {
        return new Result(HttpReturnCode.CREATED, new ObjectResultProcessor(content), HttpContentType.TEXT_HTML);
    }

    /**
     * Created (201).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result created(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.CREATED, new ObjectResultProcessor(content), contentType);
    }

    /**
     * Created (201).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result created(final JsonNode content) {
        return new Result(HttpReturnCode.CREATED, new ObjectResultProcessor(Json.toString(content)), HttpContentType.APPLICATION_JSON);
    }

    /**
     * Forbidden (403).
     *
     * @param content The content
     * @return A result
     */
    public static Result forbidden(final String content) {
        return new Result(HttpReturnCode.FORBIDDEN, new ObjectResultProcessor(content), HttpContentType.TEXT_HTML);
    }

    /**
     * Forbidden (403).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result forbidden(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.FORBIDDEN, new ObjectResultProcessor(content), contentType);
    }

    /**
     * Forbidden (403).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result forbidden(final JsonNode content) {
        return new Result(HttpReturnCode.FORBIDDEN, new ObjectResultProcessor(Json.toString(content)), HttpContentType.APPLICATION_JSON);
    }

    /**
     * Internal Server Error (500).
     *
     * @param content The content
     * @return A result
     */
    public static Result internalServerError(final String content) {
        return new Result(HttpReturnCode.INTERNAL_SERVER_ERROR, new ObjectResultProcessor(content), HttpContentType.TEXT_HTML);
    }

    /**
     * Internal Server Error (500).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result internalServerError(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.INTERNAL_SERVER_ERROR, new ObjectResultProcessor(content), contentType);
    }

    /**
     * Internal Server Error (500).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result internalServerError(final JsonNode content) {
        return new Result(HttpReturnCode.INTERNAL_SERVER_ERROR, new ObjectResultProcessor(Json.toString(content)), HttpContentType.APPLICATION_JSON);
    }

    /**
     * Internal Server Error (500).
     *
     * @param templateResult The template to render
     * @return A result
     */
    public static Result internalServerError(final TemplateResult templateResult) {
        return new Result(HttpReturnCode.NOT_FOUND, new TemplateResultProcessor(templateResult.templateName, templateResult.dataModel), HttpContentType.TEXT_HTML);
    }

    /**
     * No Content (204).
     *
     * @return A result
     */
    public static Result noContent() {
        return new Result(HttpReturnCode.NO_CONTENT, NO_CONTENT_RESULT_PROCESSOR, null);
    }

    /**
     * Not Found (404).
     *
     * @param content The content
     * @return A result
     */
    public static Result notFound(final String content) {
        return new Result(HttpReturnCode.NOT_FOUND, new ObjectResultProcessor(content), HttpContentType.TEXT_HTML);
    }

    /**
     * Not Found (404).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result notFound(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.NOT_FOUND, new ObjectResultProcessor(content), contentType);
    }

    /**
     * Not Found (404).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result notFound(final JsonNode content) {
        return new Result(HttpReturnCode.NOT_FOUND, new ObjectResultProcessor(Json.toString(content)), HttpContentType.APPLICATION_JSON);
    }

    /**
     * Not Found (404).
     *
     * @param templateResult The template to render
     * @return A result
     */
    public static Result notFound(final TemplateResult templateResult) {
        return new Result(HttpReturnCode.NOT_FOUND, new TemplateResultProcessor(templateResult.templateName, templateResult.dataModel), HttpContentType.TEXT_HTML);
    }

    /**
     * Not Implemented (501).
     *
     * @param content The content
     * @return A result
     */
    public static Result notImplemented(final String content) {
        return new Result(HttpReturnCode.NOT_IMPLEMENTED, new ObjectResultProcessor(content), HttpContentType.TEXT_HTML);
    }

    /**
     * Not Implemented (501).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result notImplemented(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.NOT_IMPLEMENTED, new ObjectResultProcessor(content), contentType);
    }

    /**
     * Not Implemented (501).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result notImplemented(final JsonNode content) {
        return new Result(HttpReturnCode.NOT_IMPLEMENTED, new ObjectResultProcessor(Json.toString(content)), HttpContentType.APPLICATION_JSON);
    }

    /**
     * Ok (200).
     *
     * @param content The content
     * @return A result
     */
    public static Result ok(final String content) {
        return new Result(HttpReturnCode.OK, new ObjectResultProcessor(content), HttpContentType.TEXT_HTML);
    }

    /**
     * Ok (200).
     *
     * @param content     The content
     * @param contentType The content type
     * @return A result
     */
    public static Result ok(final byte[] content, final String contentType) {
        return new Result(HttpReturnCode.OK, new ObjectResultProcessor(content), contentType);
    }

    /**
     * Ok (200).
     *
     * @param content     The input stream content
     * @param contentType The content type
     * @return A result
     */
    public static Result ok(final InputStream content, final String contentType) {
        return new Result(HttpReturnCode.OK, new ObjectResultProcessor(content), contentType);
    }

    /**
     * Ok (200).
     *
     * @param content The JSON content
     * @return A result
     */
    public static Result ok(final JsonNode content) {
        return new Result(HttpReturnCode.OK, new ObjectResultProcessor(Json.toString(content)), HttpContentType.APPLICATION_JSON);
    }

    /**
     * Ok (200).
     *
     * @param templateResult The template to render
     * @return A result
     */
    public static Result ok(final TemplateResult templateResult) {
        return new Result(HttpReturnCode.OK, new TemplateResultProcessor(templateResult.templateName, templateResult.dataModel), HttpContentType.TEXT_HTML);
    }

    /**
     * Redirect Permanently (301).
     *
     * @param uri The URL to redirect to
     * @return A result
     */
    public static Result redirectPermanentlyTo(final String uri) {
        return new Result(HttpReturnCode.MOVED_PERMANENTLY, NO_CONTENT_RESULT_PROCESSOR, null).withHeader("Location", uri);
    }

    /**
     * Redirect Temporary (302).
     *
     * @param uri The URL to redirect to
     * @return A result
     */
    public static Result redirectTemporaryTo(final String uri) {
        return new Result(HttpReturnCode.FOUND, NO_CONTENT_RESULT_PROCESSOR, null).withHeader("Location", uri);
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
     * Get tje result processor.
     *
     * @return The result processor
     */
    public ResultProcessor getResultProcessor() {
        return this.resultProcessor;
    }

    /**
     * Result processor. In charge to transform a content (any type) into an {@code InputStream}.
     */
    public interface ResultProcessor {

        /**
         * Process the result.
         *
         * @param context          The current context
         * @param templateRenderer The template rendered if available
         */
        void process(final Context context, final TemplateRenderer templateRenderer);

        /**
         * Get the result input stream.
         *
         * @return The result input stream
         */
        InputStream getInputStream();
    }

    /**
     * No content ("do nothing" processor).
     */
    private static class NoContentResultProcessor implements ResultProcessor {

        @Override
        public void process(final Context context, final TemplateRenderer templateRenderer) {
        }

        @Override
        public InputStream getInputStream() {
            return null;
        }
    }

    /**
     * Process a simple object.
     */
    private static class ObjectResultProcessor implements ResultProcessor {

        private final Object object;

        /**
         * Build a new instance.
         *
         * @param object Object to process
         */
        public ObjectResultProcessor(final Object object) {
            this.object = object;
        }

        @Override
        public void process(final Context context, final TemplateRenderer templateRenderer) {
        }

        @Override
        public InputStream getInputStream() {
            if (object == null) {
                return ByteArrayInputStream.nullInputStream();
            } else if (object instanceof byte[]) {
                return new ByteArrayInputStream((byte[]) object);
            } else if (object instanceof InputStream) {
                return (InputStream) object;
            }

            return new ByteArrayInputStream(object.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Process a template.
     */
    private static class TemplateResultProcessor implements ResultProcessor {

        private final String templateName;
        private final Map<String, Object> dataModel;

        private InputStream inputStream;

        /**
         * Build a new instance.
         *
         * @param templateName The name of the template to render
         * @param dataModel    The data model to use
         */
        public TemplateResultProcessor(final String templateName, final Map<String, Object> dataModel) {
            this.templateName = templateName;
            this.dataModel = dataModel;
            this.inputStream = null;
        }

        @Override
        public void process(final Context context, final TemplateRenderer templateRenderer) {
            if (templateRenderer == null) {
                throw new TemplateException.NoTemplateEngine();
            }

            this.dataModel.put("flash", context.getFlashMessages());

            final String renderedTemplate = templateRenderer.render(this.templateName, context.getLocale(), this.dataModel);
            this.inputStream = new ByteArrayInputStream(renderedTemplate.getBytes(StandardCharsets.UTF_8));

            context.getFlashMessages().clear();
        }

        @Override
        public InputStream getInputStream() {
            return this.inputStream;
        }
    }
}
