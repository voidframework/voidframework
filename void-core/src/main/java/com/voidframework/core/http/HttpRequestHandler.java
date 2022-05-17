package com.voidframework.core.http;

/**
 * An HTTP request handler.
 */
public interface HttpRequestHandler {

    Result onRouteRequest(final HttpRequest httpRequest);
}
