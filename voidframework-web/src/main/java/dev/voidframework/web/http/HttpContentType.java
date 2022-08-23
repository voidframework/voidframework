package dev.voidframework.web.http;

/**
 * Defines some HTTP content types.
 */
public final class HttpContentType {

    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_X_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_ZIP = "application/zip";
    public static final String IMAGE_ICON = "image/x-icon";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_PNG = "image/png";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_YAML = "text/yaml";

    /**
     * Default constructor.
     */
    private HttpContentType() {

        throw new UnsupportedOperationException("This is a class containing constants and cannot be instantiated");
    }
}
