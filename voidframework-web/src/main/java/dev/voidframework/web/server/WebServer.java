package dev.voidframework.web.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.core.conversion.ConverterManager;
import dev.voidframework.core.helper.ClassResolver;
import dev.voidframework.core.lifecycle.LifeCycleStart;
import dev.voidframework.core.lifecycle.LifeCycleStop;
import dev.voidframework.web.exception.ErrorHandlerException;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Cookie;
import dev.voidframework.web.http.ErrorHandler;
import dev.voidframework.web.http.FormItem;
import dev.voidframework.web.http.HttpRequest;
import dev.voidframework.web.http.HttpRequestBodyContent;
import dev.voidframework.web.http.HttpRequestHandler;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.converter.StringToBooleanConverter;
import dev.voidframework.web.http.converter.StringToByteConverter;
import dev.voidframework.web.http.converter.StringToCharacterConverter;
import dev.voidframework.web.http.converter.StringToDoubleConverter;
import dev.voidframework.web.http.converter.StringToFloatConverter;
import dev.voidframework.web.http.converter.StringToIntegerConverter;
import dev.voidframework.web.http.converter.StringToLongConverter;
import dev.voidframework.web.http.converter.StringToShortConverter;
import dev.voidframework.web.http.converter.StringToUUIDConverter;
import dev.voidframework.web.routing.AppRoutesDefinition;
import dev.voidframework.web.routing.Router;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.CookieImpl;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Web server.
 */
@Singleton
public class WebServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);
    private static final Duration COOKIE_LANG_DURATION = Duration.ofDays(365);

    private final Config configuration;
    private final Injector injector;

    private boolean isRunning;
    private Undertow undertowServer;
    private HttpRequestHandler httpRequestHandler;

    @Inject
    public WebServer(final Config configuration, final Injector injector) {

        this.configuration = configuration;
        this.injector = injector;
        this.undertowServer = null;
        this.httpRequestHandler = null;
    }

    @LifeCycleStart
    public void startWebServer() {

        if (this.isRunning) {
            LOGGER.info("Web Daemon is already started!");
            return;
        }

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

        // Load custom routes
        if (this.configuration.hasPath("voidframework.web.routes")) {
            final Router router = this.injector.getInstance(Router.class);
            this.configuration.getStringList("voidframework.web.routes")
                .stream()
                .filter(StringUtils::isNotEmpty)
                .forEach(appRoutesDefinitionClassName -> {
                    final Class<?> abstractRoutesDefinitionClass = ClassResolver.forName(appRoutesDefinitionClassName);
                    if (abstractRoutesDefinitionClass == null) {
                        throw new RuntimeException("Can't find routes definition '" + appRoutesDefinitionClassName + "'");
                    }

                    final AppRoutesDefinition appRoutesDefinition = (AppRoutesDefinition) this.injector.getInstance(abstractRoutesDefinitionClass);
                    appRoutesDefinition.defineAppRoutes(router);
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

            String contentType = httpServerExchange.getRequestHeaders().getLast("Content-Type");
            if (contentType != null) {
                contentType = contentType.split(";")[0];
            }

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

                httpRequest = new UndertowRequest(
                    httpServerExchange,
                    new HttpRequestBodyContent(contentType, null, formItemPerKeyMap));

            } else {
                final byte[] content = httpServerExchange.getInputStream().readAllBytes();
                httpRequest = new UndertowRequest(
                    httpServerExchange,
                    new HttpRequestBodyContent(contentType, content, null));
            }

            // Build Context
            final Locale i18nLocale;
            final List<String> availableLanguageList = this.configuration.getStringList("voidframework.web.i18n.languages");
            Cookie i18nCookie = httpRequest.getCookie(this.configuration.getString("voidframework.web.i18n.languageCookieName"));
            if (i18nCookie != null && availableLanguageList.contains(i18nCookie.value())) {
                i18nLocale = Locale.forLanguageTag(i18nCookie.value());
            } else {
                i18nLocale = availableLanguageList.isEmpty() ? null : Locale.forLanguageTag(availableLanguageList.get(0));
            }

            final Context context = new Context(httpRequest, i18nLocale);

            // Process request
            final Result result = httpRequestHandler.onRouteRequest(context);

            // Check if exchange is still available
            if (httpServerExchange.isComplete()) {
                return;
            }

            // Set the return HttpCode and Content-Type
            httpServerExchange.setStatusCode(result.getHttpCode());
            httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, result.getContentType());

            // Headers
            for (final Map.Entry<String, String> entrySet : result.getHeader().entrySet()) {
                httpServerExchange.getResponseHeaders().put(
                    new HttpString(entrySet.getKey()),
                    entrySet.getValue());
            }

            // Persist locale to Cookie
            if (context.getLocale() != null) {
                i18nCookie = Cookie.of(
                    this.configuration.getString("voidframework.web.i18n.languageCookieName"),
                    context.getLocale().toLanguageTag(),
                    this.configuration.getBoolean("voidframework.web.i18n.languageCookieHttpOnly"),
                    this.configuration.getBoolean("voidframework.web.i18n.languageCookieSecure"),
                    COOKIE_LANG_DURATION);

                result.withCookie(i18nCookie);
            }

            // Cookies
            for (final Cookie cookie : result.getCookie().values()) {
                CookieImpl cookieImpl = new CookieImpl(cookie.name(), cookie.value())
                    .setDomain(cookie.domain())
                    .setPath(cookie.path())
                    .setHttpOnly(cookie.isHttpOnly())
                    .setSecure(cookie.isSecure())
                    .setDiscard(cookie.timeToLive() == Duration.ZERO);
                if (cookie.timeToLive() != null) {
                    cookieImpl = cookieImpl.setMaxAge((int) cookie.timeToLive().toSeconds());
                }

                httpServerExchange.setResponseCookie(cookieImpl);
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
            .setServerOption(UndertowOptions.SHUTDOWN_TIMEOUT, this.configuration.getInt("voidframework.web.gracefulStopTimeout"))
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

    @LifeCycleStop(gracefulStopTimeoutConfigKey = "voidframework.web.gracefulStopTimeout")
    public void stopWebServer() {
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
