package dev.voidframework.web.http;

import io.undertow.websockets.core.WebSocketChannel;

/**
 * A WebSocket request context.
 *
 * @since 1.3.0
 */
public final class WebSocketContext {

    private final WebSocketRequest webSocketRequest;
    private final WebSocketChannel webSocketChannel;

    /**
     * Build a new instance.
     *
     * @param webSocketRequest The WebSocket request
     * @param webSocketChannel The WebSocket channel
     * @since 1.3.0
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
     * @since 1.3.0
     */
    public WebSocketRequest getRequest() {

        return this.webSocketRequest;
    }

    /**
     * Retrieves the WebSocket channel.
     *
     * @return The WebSocket channel
     * @since 1.3.0
     */
    public WebSocketChannel getChannel() {

        return this.webSocketChannel;
    }
}
