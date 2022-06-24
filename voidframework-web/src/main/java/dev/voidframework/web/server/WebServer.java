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
import dev.voidframework.web.exception.FilterException;
import dev.voidframework.web.exception.RoutingException;
import dev.voidframework.web.filter.Filter;
import dev.voidframework.web.http.ErrorHandler;
import dev.voidframework.web.http.HttpRequestHandler;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Web server.
 */
@Singleton
public class WebServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);

    private final Config configuration;
    private final Injector injector;

    private boolean isRunning;
    private Undertow undertowServer;
    private HttpRequestHandler httpRequestHandler;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @param injector      The injector instance
     */
    @Inject
    public WebServer(final Config configuration, final Injector injector) {

        this.configuration = configuration;
        this.injector = injector;
        this.undertowServer = null;
        this.httpRequestHandler = null;
    }

    /**
     * Start the web server.
     */
    @LifeCycleStart(priority = 800)
    @SuppressWarnings("unused")
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

        // Retrieves global filters
        final List<Class<? extends Filter>> globalFilterList = new ArrayList<>();
        if (this.configuration.hasPath("voidframework.web.globalFilters")) {
            for (final String filterName : this.configuration.getStringList("voidframework.web.globalFilters")) {
                final Class<? extends Filter> filterClass = ClassResolver.forName(filterName);
                if (filterClass == null) {
                    throw new FilterException.LoadFailure(filterName);
                }

                globalFilterList.add(filterClass);
            }
        }

        // Instantiate the Http request handler
        this.httpRequestHandler = new HttpRequestHandler(this.injector, errorHandler, globalFilterList);

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
                        throw new RoutingException.AppRouteDefinitionLoadFailure(appRoutesDefinitionClassName);
                    }

                    final AppRoutesDefinition appRoutesDefinition = (AppRoutesDefinition) this.injector.getInstance(abstractRoutesDefinitionClass);
                    appRoutesDefinition.defineAppRoutes(router);
                });
        }

        // Defines the HTTP handler
        final HttpHandler httpHandler = new UndertowHttpHandler(
            this.configuration,
            this.httpRequestHandler,
            new SessionSigner(this.configuration));

        // Configure Undertow
        this.undertowServer = Undertow.builder()
            .setServerOption(UndertowOptions.SHUTDOWN_TIMEOUT, this.configuration.getInt("voidframework.web.gracefulStopTimeout"))
            .setServerOption(UndertowOptions.MULTIPART_MAX_ENTITY_SIZE, this.configuration.getMemorySize("voidframework.web.maxBodySize").toBytes())
            .setServerOption(UndertowOptions.MAX_ENTITY_SIZE, this.configuration.getMemorySize("voidframework.web.maxBodySize").toBytes())
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

    /**
     * Stop the web server.
     */
    @LifeCycleStop(gracefulStopTimeoutConfigKey = "voidframework.web.gracefulStopTimeout")
    @SuppressWarnings("unused")
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
