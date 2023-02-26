package dev.voidframework.web.http;

/**
 * Defines some HTTP return codes.
 *
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class HttpReturnCode {

    // 1xx
    /**
     * @since 1.2.0
     */
    public static final int CONTINUE = 100;

    /**
     * @since 1.2.0
     */
    public static final int SWITCHING_PROTOCOLS = 101;

    // 2xx
    /**
     * @since 1.0.0
     */
    public static final int OK = 200;

    /**
     * @since 1.0.0
     */
    public static final int CREATED = 201;

    /**
     * @since 1.2.0
     */
    public static final int ACCEPTED = 202;

    /**
     * @since 1.2.0
     */
    public static final int NON_AUTHORITATIVE_INFORMATION = 203;

    /**
     * @since 1.0.0
     */
    public static final int NO_CONTENT = 204;

    /**
     * @since 1.2.0
     */
    public static final int RESET_CONTENT = 205;

    // 3xx
    public static final int MULTIPLE_CHOICES = 300;

    /**
     * @since 1.0.0
     */
    public static final int MOVED_PERMANENTLY = 301;

    /**
     * @since 1.0.0
     */
    public static final int FOUND = 302;

    /**
     * @since 1.2.0
     */
    public static final int SEE_OTHER = 303;

    /**
     * @since 1.2.0
     */
    public static final int NOT_MODIFIED = 304;

    /**
     * @since 1.2.0
     */
    public static final int USE_PROXY = 305;

    /**
     * @since 1.2.0
     */
    public static final int SWITCH_PROXY = 306;

    /**
     * @since 1.2.0
     */
    public static final int TEMPORARY_REDIRECT = 307;

    /**
     * @since 1.2.0
     */
    public static final int PERMANENT_REDIRECT = 308;

    // 4xx
    /**
     * @since 1.0.0
     */
    public static final int BAD_REQUEST = 400;

    /**
     * @since 1.2.0
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * @since 1.2.0
     */
    public static final int PAYMENT_REQUIRED = 402;

    /**
     * @since 1.0.0
     */
    public static final int FORBIDDEN = 403;

    /**
     * @since 1.0.0
     */
    public static final int NOT_FOUND = 404;

    /**
     * @since 1.0.0
     */
    public static final int METHOD_NOT_ALLOWED = 405;

    /**
     * @since 1.2.0
     */
    public static final int NOT_ACCEPTABLE = 406;

    /**
     * @since 1.2.0
     */
    public static final int PROXY_AUTHENTICATION_REQUIRED = 408;

    /**
     * @since 1.2.0
     */
    public static final int REQUEST_TIMEOUT = 408;

    /**
     * @since 1.2.0
     */
    public static final int CONFLICT = 409;

    /**
     * @since 1.2.0
     */
    public static final int GONE = 410;

    /**
     * @since 1.0.0
     */
    public static final int I_AM_A_TEAPOT = 418;

    // 5xx
    /**
     * @since 1.0.0
     */
    public static final int INTERNAL_SERVER_ERROR = 500;

    /**
     * @since 1.0.0
     */
    public static final int NOT_IMPLEMENTED = 501;

    /**
     * @since 1.2.0
     */
    public static final int BAD_GATEWAY = 502;

    /**
     * @since 1.2.0
     */
    public static final int SERVICE_UNAVAILABLE = 503;

    /**
     * @since 1.2.0
     */
    public static final int GATEWAY_TIMEOUT = 504;

    /**
     * @since 1.2.0
     */
    public static final int HTTP_VERSION_NOT_SUPPORTED = 505;

    /**
     * Default constructor.
     *
     * @since 1.2.0
     */
    private HttpReturnCode() {

        throw new UnsupportedOperationException("This is a class containing constants and cannot be instantiated");
    }
}
