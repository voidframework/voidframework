package com.voidframework.core.http;

/**
 * An HTTP request handler.
 */
public interface HttpRequestHandler {

    String onRouteRequest(final Context context);
}
