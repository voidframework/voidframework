package dev.voidframework.web.server.http;

import dev.voidframework.web.http.WebSocketContext;
import dev.voidframework.web.http.WebSocketRequest;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.io.IOException;

/**
 * Undertow WebSocket callback implementation.
 *
 * @since 1.3.0
 */
public class UndertowWebSocketCallback implements WebSocketConnectionCallback {

    private final HttpWebSocketRequestHandler wsSocketIncomingConnHandler;

    /**
     * Build a new instance.
     *
     * @param wsSocketIncomingConnHandler The WebSocket incoming connection handler
     * @since 1.3.0
     */
    public UndertowWebSocketCallback(final HttpWebSocketRequestHandler wsSocketIncomingConnHandler) {

        this.wsSocketIncomingConnHandler = wsSocketIncomingConnHandler;
    }

    @Override
    public void onConnect(final WebSocketHttpExchange webSocketHttpExchange, final WebSocketChannel webSocketChannel) {

        final WebSocketRequest wsRequest = new UndertowWebSocketRequest(webSocketHttpExchange);
        final WebSocketContext context = new WebSocketContext(wsRequest, webSocketChannel);

        try {
            wsSocketIncomingConnHandler.onIncomingConnection(context);
        } catch (final IOException ignore) {
            // Nothing to do
        }
    }
}
