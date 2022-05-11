package com.voidframework.undertow;

import com.voidframework.core.ApplicationLauncher;
import io.undertow.Undertow;
import io.undertow.util.Headers;

public class UndertowVoidFramework {

    public static void main(String[] args) {
        final ApplicationLauncher applicationLauncher = new ApplicationLauncher();
        Runtime.getRuntime().addShutdownHook(new Thread(applicationLauncher::stop));

        applicationLauncher.launch((configuration, logger) -> {
            final Undertow server = Undertow.builder()
                // Set up the listener - you can change the port/host here
                // 0.0.0.0 means "listen on ALL available addresses"
                .addHttpListener(
                    configuration.getInt("voidframework.undertow.listen.port"),
                    configuration.getString("voidframework.undertow.listen.host"))
                .setHandler(httpServerExchange -> {
                    // Sets the return Content-Type to text/html
                    httpServerExchange.getResponseHeaders()
                        .put(Headers.SERVER, "voidframework")
                        .put(Headers.CONTENT_TYPE, "text/html");

                    // Returns a hard-coded HTML document
                    httpServerExchange.getResponseSender()
                        .send("<html>" +
                            "<body>" +
                            "<h1>Hello, world!</h1>" +
                            "</body>" +
                            "</html>");
                })
                .build();

            // Boot the web server
            server.start();

            // Listen
            for (final Undertow.ListenerInfo listenerInfo : server.getListenerInfo()) {
                logger.info("Server now listening on {}:/{}", listenerInfo.getProtcol(), listenerInfo.getAddress());
            }
        });
    }
}
