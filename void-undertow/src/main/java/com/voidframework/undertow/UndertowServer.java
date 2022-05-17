package com.voidframework.undertow;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import com.voidframework.core.ApplicationLauncher;
import com.voidframework.core.http.HttpRequestHandler;
import com.voidframework.core.http.Result;
import com.voidframework.core.server.ListenerInformation;
import com.voidframework.core.server.Server;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Undertow {@link Server} implementation.
 */
public final class UndertowServer implements Server {

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
            httpServerExchange.startBlocking();

            final Result result;

            // Try to parse content
            final MultiPartParserDefinition multiPartParserDefinition = new MultiPartParserDefinition()
                //.setTempFileLocation(new File(System.getProperty("java.io.tmpdir")).toPath())
                .setTempFileLocation(null)
                .setDefaultEncoding("UTF-8");

            multiPartParserDefinition.setFileSizeThreshold(1200000000);

            final FormDataParser formDataParser = FormParserFactory.builder(false)
                .addParser(multiPartParserDefinition)
                .build()
                .createParser(httpServerExchange);
            if (formDataParser != null) {
                final FormData formData = formDataParser.parseBlocking();
                System.err.println(formData);

                final Map<String, List<String>> data = new HashMap<>();
                for (final String formDataKey : formData) {
                    final List<String> list = data.computeIfAbsent(formDataKey, k -> new ArrayList<>());

                    for (final FormData.FormValue toto : formData.get(formDataKey)) {
                        if (!toto.isFileItem()) {
                            list.add(toto.getValue());
                        }
                    }
                }

                result = httpRequestHandler.onRouteRequest(new UndertowRequest(httpServerExchange, null));

            } else {
                final byte[] content = httpServerExchange.getInputStream().readAllBytes();
                result = httpRequestHandler.onRouteRequest(new UndertowRequest(httpServerExchange, content));
            }

            // Sets the return Content-Type to text/html
            httpServerExchange.setStatusCode(result.getHttpCode());
            httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, result.getContentType());
            for (final Map.Entry<String, String> entrySet : result.getHeader().entrySet()) {
                httpServerExchange.getResponseHeaders().put(
                    new HttpString(entrySet.getKey()),
                    entrySet.getValue());
            }

            // Returns content
            final OutputStream outputStream = httpServerExchange.getOutputStream();
            final InputStream inputStream = result.getInputStream();

            httpServerExchange.setResponseContentLength(inputStream.available());

            final byte[] buffer = new byte[8192];
            int readLength;
            while ((readLength = inputStream.read(buffer, 0, buffer.length)) > 0) {
                outputStream.write(buffer, 0, readLength);
                outputStream.flush();
            }

            outputStream.close();
            inputStream.close();
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
