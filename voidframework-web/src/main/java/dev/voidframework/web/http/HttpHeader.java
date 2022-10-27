package dev.voidframework.web.http;

/**
 * Defines some HTTP headers.
 */
@SuppressWarnings("unused")
public final class HttpHeader {

    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_CHARSET = "Accept-Charset";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String AGE = "Age";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CONTENT_LANGUAGE = "Content-Language";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_LOCATION = "Content-Location";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String COOKIE = "Cookie";
    public static final String ETAG = "Etag";
    public static final String EXPIRES = "Expires";
    public static final String HOST = "Host";
    public static final String LINK = "Link";
    public static final String LOCATION = "Location";
    public static final String ORIGIN = "Origin";
    public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
    public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
    public static final String REFERER = "Referer";
    public static final String SERVER = "Server";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String USER_AGENT = "User-Agent";
    public static final String X_CSRF_TOKEN = "X-CSRF-TOKEN";

    /**
     * Default constructor.
     */
    private HttpHeader() {

        throw new UnsupportedOperationException("This is a class containing constants and cannot be instantiated");
    }
}
