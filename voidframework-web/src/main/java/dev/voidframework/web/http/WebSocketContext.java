package dev.voidframework.web.http;

import io.undertow.websockets.core.WebSocketChannel;

/**
 * A WebSocket request context.
 */
public final class WebSocketContext {

    private final WebSocketRequest webSocketRequest;
    private final WebSocketChannel webSocketChannel;

    /**
     * Build a new instance.
     *
     * @param webSocketRequest The WebSocket request
     * @param webSocketChannel The WebSocket channel
     */
    public WebSocketContext(final WebSocketRequest webSocketRequest,
                            final WebSocketChannel webSocketChannel) {

        this.webSocketRequest = webSocketRequest;
        this.webSocketChannel = webSocketChannel;
    }

    /**
     * Retrieves the request.
     *
     * @return The current request
     */
    public WebSocketRequest getRequest() {

        return this.webSocketRequest;
    }

    /**
     * Retrieves the WebSocket channel.
     *
     * @return The WebSocket channel
     */
    public WebSocketChannel getChannel() {

        return this.webSocketChannel;
    }
}
