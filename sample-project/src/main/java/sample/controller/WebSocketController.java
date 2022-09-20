package sample.controller;

import com.google.inject.Singleton;
import dev.voidframework.web.bindable.WebController;
import dev.voidframework.web.http.HttpMethod;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.TemplateResult;
import dev.voidframework.web.http.WebSocketContext;
import dev.voidframework.web.http.annotation.RequestRoute;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

/**
 * A simple "WebSocket" web controller.
 */
@Singleton
@WebController
public class WebSocketController {

    /**
     * Display WebSocket page.
     *
     * @return A Result
     */
    @RequestRoute(method = HttpMethod.GET, route = "/ws")
    public Result showWebSocketPage() {
        return Result.ok(TemplateResult.of("websocket.ftl"));
    }

    /**
     * Handles new WebSocket connection.
     */
    @RequestRoute(method = HttpMethod.WEBSOCKET, route = "/ws")
    public void onConnect(final WebSocketContext context) {

        context.getChannel().getReceiveSetter().set(new AbstractReceiveListener() {

            @Override
            protected void onFullTextMessage(final WebSocketChannel channel, final BufferedTextMessage message) {
                final String messageData = message.getData();
                for (final WebSocketChannel session : channel.getPeerConnections()) {
                    WebSockets.sendText(messageData, session, null);
                }
            }
        });
        context.getChannel().resumeReceives();
    }
}
