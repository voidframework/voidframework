package dev.voidframework.web.http;

/**
 * Defines some HTTP return codes.
 */
public interface HttpReturnCode {

    int OK = 200;
    int CREATED = 201;
    int NO_CONTENT = 204;
    int MOVED_PERMANENTLY = 301;
    int FOUND = 302;
    int BAD_REQUEST = 400;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;
    int METHOD_NOT_ALLOWED = 405;
    int INTERNAL_SERVER_ERROR = 500;
    int NOT_IMPLEMENTED = 501;
    int I_AM_A_TEAPOT = 418;
}
