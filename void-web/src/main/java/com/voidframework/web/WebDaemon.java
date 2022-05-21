package com.voidframework.web;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.typesafe.config.Config;
import com.voidframework.core.conversion.ConverterManager;
import com.voidframework.core.daemon.Daemon;
import com.voidframework.core.helper.ClassResolver;
import com.voidframework.web.exception.ErrorHandlerException;
import com.voidframework.web.http.ErrorHandler;
import com.voidframework.web.http.FormItem;
import com.voidframework.web.http.HttpRequest;
import com.voidframework.web.http.HttpRequestBodyContent;
import com.voidframework.web.http.Result;
import com.voidframework.web.http.converter.StringToBooleanConverter;
import com.voidframework.web.http.converter.StringToByteConverter;
import com.voidframework.web.http.converter.StringToCharacterConverter;
import com.voidframework.web.http.converter.StringToDoubleConverter;
import com.voidframework.web.http.converter.StringToFloatConverter;
import com.voidframework.web.http.converter.StringToIntegerConverter;
import com.voidframework.web.http.converter.StringToLongConverter;
import com.voidframework.web.http.converter.StringToShortConverter;
import com.voidframework.web.http.converter.StringToUUIDConverter;
import com.voidframework.web.http.impl.HttpRequestHandler;
import com.voidframework.web.routing.AppRoutesDefinition;
import com.voidframework.web.routing.Router;
import com.voidframework.web.routing.impl.DefaultRouter;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WebDaemon implements Daemon {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDaemon.class);

    private Config configuration;
    private Injector injector;

    private boolean isRunning;
    private Undertow undertowServer;
    private HttpRequestHandler httpRequestHandler;

    public WebDaemon() {

        this.isRunning = false;
        this.undertowServer = null;
        this.httpRequestHandler = null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public long getGracefulStopTimeout() {
        return this.configuration.getInt("voidframework.web.gracefulStopTimeout");
    }

    @Override
    public Module getModule() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(Router.class).to(DefaultRouter.class).asEagerSingleton();
            }
        };
    }

    @Override
    public void configure(final Config configuration, final Injector injector) {

        this.configuration = configuration;
        this.injector = injector;

        // Instantiate the error handler
        final String errorHandlerClassName = configuration.getString("voidframework.web.errorHandler");
        final Class<?> errorHandlerClass = ClassResolver.forName(errorHandlerClassName);
        if (errorHandlerClass == null) {
            throw new ErrorHandlerException.ClassNotFound(errorHandlerClassName);
        } else if (!ErrorHandler.class.isAssignableFrom(errorHandlerClass)) {
            throw new ErrorHandlerException.InvalidClass(errorHandlerClassName);
        }

        final ErrorHandler errorHandler;
        try {
            errorHandler = (ErrorHandler) this.injector.getInstance(errorHandlerClass);
            if (errorHandler == null) {
                throw new ErrorHandlerException.CantInstantiate(errorHandlerClassName);
            }
        } catch (final Exception exception) {
            throw new ErrorHandlerException.CantInstantiate(errorHandlerClassName, exception);
        }

        // Instantiate the Http request handler
        this.httpRequestHandler = new HttpRequestHandler(this.injector, errorHandler);

        // Built-in converters
        final ConverterManager converterManager = this.injector.getInstance(ConverterManager.class);
        converterManager.registerConverter(String.class, Boolean.class, new StringToBooleanConverter());
        converterManager.registerConverter(String.class, Byte.class, new StringToByteConverter());
        converterManager.registerConverter(String.class, Character.class, new StringToCharacterConverter());
        converterManager.registerConverter(String.class, Double.class, new StringToDoubleConverter());
        converterManager.registerConverter(String.class, Float.class, new StringToFloatConverter());
        converterManager.registerConverter(String.class, Integer.class, new StringToIntegerConverter());
        converterManager.registerConverter(String.class, Long.class, new StringToShortConverter());
        converterManager.registerConverter(String.class, Short.class, new StringToLongConverter());
        converterManager.registerConverter(String.class, UUID.class, new StringToUUIDConverter());
    }

    @Override
    public void run() {
        if (this.isRunning) {
            LOGGER.error("Web Daemon has already started!");
            return;
        }

        // Load app defined routes
        if (this.configuration.hasPath("voidframework.web.routes")) {
            final Router router = this.injector.getInstance(Router.class);
            this.configuration.getStringList("voidframework.web.routes")
                .stream()
                .filter(StringUtils::isNotEmpty)
                .forEach(appRoutesDefinitionClassName -> {
                    try {
                        final Class<?> abstractRoutesDefinitionClass = Class.forName(appRoutesDefinitionClassName);
                        final AppRoutesDefinition appRoutesDefinition = (AppRoutesDefinition) this.injector.getInstance(abstractRoutesDefinitionClass);
                        appRoutesDefinition.defineAppRoutes(router);
                    } catch (final ClassNotFoundException ex) {
                        throw new RuntimeException("Can't find routes definition '" + appRoutesDefinitionClassName + "'", ex);
                    }
                });
        }

        // Defines the HTTP handler
        final HttpHandler httpHandler = httpServerExchange -> {
            httpServerExchange.startBlocking();

            final HttpRequest httpRequest;

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
                final Map<String, List<FormItem>> formItemPerKeyMap = new HashMap<>();
                final FormData formData = formDataParser.parseBlocking();
                for (final String formDataKey : formData) {
                    final List<FormItem> formItemList = formItemPerKeyMap.computeIfAbsent(formDataKey, k -> new ArrayList<>());
                    for (final FormData.FormValue formValue : formData.get(formDataKey)) {
                        if (formValue.isFileItem()) {
                            formItemList.add(new FormItem(null, formValue.getCharset(), formValue.isFileItem(), formValue.getFileItem().getInputStream()));
                        } else {
                            formItemList.add(new FormItem(formValue.getValue(), formValue.getCharset(), formValue.isFileItem(), null));
                        }
                    }
                }

                httpRequest = new UndertowRequest(httpServerExchange, new HttpRequestBodyContent(null, formItemPerKeyMap));

            } else {
                final byte[] content = httpServerExchange.getInputStream().readAllBytes();
                httpRequest = new UndertowRequest(httpServerExchange, new HttpRequestBodyContent(content, null));
            }

            // Process request
            final Result result = httpRequestHandler.onRouteRequest(httpRequest);

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
        this.undertowServer = Undertow.builder()
            .setServerOption(UndertowOptions.SHUTDOWN_TIMEOUT, (int) getGracefulStopTimeout())
            .addHttpListener(
                configuration.getInt("voidframework.web.server.listenPort"),
                configuration.getString("voidframework.web.server.listenHost"))
            .setHandler(httpServerExchange -> httpServerExchange.dispatch(httpHandler))
            .build();

        // Boot the web server
        this.undertowServer.start();

        // Display listener(s) information
        for (final Undertow.ListenerInfo listenerInfo : undertowServer.getListenerInfo()) {
            LOGGER.info("Server now listening on {}:/{}", listenerInfo.getProtcol(), listenerInfo.getAddress());
        }

        this.isRunning = true;
    }

    @Override
    public void gracefulStop() {
        if (this.undertowServer != null) {
            this.undertowServer.stop();
            this.undertowServer = null;
            this.httpRequestHandler = null;
            this.isRunning = false;
        } else {
            LOGGER.info("Web Daemon is already stopped!");
        }
    }
}
