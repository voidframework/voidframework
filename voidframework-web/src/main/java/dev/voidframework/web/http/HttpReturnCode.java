package dev.voidframework.web.http;

/**
 * Defines some HTTP return codes.
 */
public final class HttpReturnCode {

    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int NO_CONTENT = 204;
    public static final int MOVED_PERMANENTLY = 301;
    public static final int FOUND = 302;
    public static final int BAD_REQUEST = 400;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int NOT_IMPLEMENTED = 501;
    public static final int I_AM_A_TEAPOT = 418;

    /**
     * Default constructor.
     */
    private HttpReturnCode() {

        throw new UnsupportedOperationException("This is a class containing constants and cannot be instantiated");
    }
}
