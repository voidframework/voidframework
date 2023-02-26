package dev.voidframework.web.http;

/**
 * Defines some HTTP content types.
 *
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class HttpContentTypes {

    /**
     * @since 1.2.0
     */
    public static final String APPLICATION_ATOM_XML = "application/atom+xml";

    /**
     * @since 1.5.0
     */
    public static final String APPLICATION_JAVASCRIPT = "application/javascript";

    /**
     * @since 1.0.0
     */
    public static final String APPLICATION_JSON = "application/json";

    /**
     * @since 1.0.0
     */
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    /**
     * @since 1.2.0
     */
    public static final String APPLICATION_PDF = "application/pdf";

    /**
     * @since 1.2.0
     */
    public static final String APPLICATION_RSS_XML = "application/rss+xml";

    /**
     * @since 1.3.0
     */
    public static final String APPLICATION_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    /**
     * @since 1.0.0
     */
    public static final String APPLICATION_XML = "application/xml";

    /**
     * @since 1.0.0
     */
    public static final String APPLICATION_ZIP = "application/zip";

    /**
     * @since 1.2.0
     */
    public static final String IMAGE_GIF = "image/gif";

    /**
     * @since 1.0.0
     */
    public static final String IMAGE_ICON = "image/x-icon";

    /**
     * @since 1.0.0
     */
    public static final String IMAGE_JPEG = "image/jpeg";

    /**
     * @since 1.0.0
     */
    public static final String IMAGE_PNG = "image/png";

    /**
     * @since 1.0.0
     */
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    /**
     * @since 1.0.0
     */
    public static final String TEXT_HTML = "text/html";

    /**
     * @since 1.2.0
     */
    public static final String TEXT_MARKDOWN = "text/markdown";

    /**
     * @since 1.2.0
     */
    public static final String TEXT_PLAIN = "text/plain";

    /**
     * @since 1.0.0
     */
    public static final String TEXT_YAML = "text/yaml";

    /**
     * Default constructor.
     *
     * @since 1.2.0
     */
    private HttpContentTypes() {

        throw new UnsupportedOperationException("This is a class containing constants and cannot be instantiated");
    }
}
