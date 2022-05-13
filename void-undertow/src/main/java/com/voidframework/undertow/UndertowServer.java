package com.voidframework.undertow;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import com.voidframework.core.ApplicationLauncher;
import com.voidframework.core.http.HttpRequestHandler;
import com.voidframework.core.server.ListenerInformation;
import com.voidframework.core.server.Server;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;

import java.util.List;

/**
 * Undertow context implementation.
 */
public class UndertowServer implements Server {

    private final Config configuration;
    private final HttpRequestHandler httpRequestHandler;

    private Undertow undertowServer;

    @Inject
    public UndertowServer(final Config configuration, final HttpRequestHandler httpRequestHandler) {
        this.configuration = configuration;
        this.httpRequestHandler = httpRequestHandler;
    }

    /**
     * Entry point.
     *
     * @param args Arguments
     */
    public static void main(final String[] args) {
        final ApplicationLauncher applicationLauncher = new ApplicationLauncher();
        applicationLauncher.launch();
    }

    @Override
    public List<ListenerInformation> start() {
        if (undertowServer != null) {
            throw new RuntimeException("Server already running!");
        }

        // Defines the HTTP handler
        final HttpHandler httpHandler = httpServerExchange -> {
            final String data = httpRequestHandler.onRouteRequest(new UndertowContext(httpServerExchange));

            // Sets the return Content-Type to text/html
            httpServerExchange.getResponseHeaders()
                .put(Headers.SERVER, "voidframework")
                .put(Headers.CONTENT_TYPE, "text/html");

            // Returns a hard-coded HTML document
            httpServerExchange.getResponseSender().send(data);
        };

        // Configure Undertow
        undertowServer = Undertow.builder()
            .addHttpListener(
                configuration.getInt("voidframework.undertow.listenPort"),
                configuration.getString("voidframework.undertow.listenHost"))
            .setHandler(httpServerExchange -> httpServerExchange.dispatch(httpHandler))
            .build();

        // Boot the web server
        undertowServer.start();

        // Return listener(s) information
        return undertowServer.getListenerInfo()
            .stream()
            .map(listenerInfo -> new ListenerInformation(listenerInfo.getProtcol(), listenerInfo.getAddress().toString()))
            .toList();
    }

    @Override
    public void onStop() {
        if (undertowServer != null) {
            undertowServer.stop();
        }
    }
}
