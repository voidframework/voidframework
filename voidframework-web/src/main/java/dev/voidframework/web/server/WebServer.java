package dev.voidframework.web.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.core.bindable.BindClass;
import dev.voidframework.core.conversion.ConverterManager;
import dev.voidframework.core.helper.ClassResolver;
import dev.voidframework.core.lifecycle.LifeCycleStart;
import dev.voidframework.core.lifecycle.LifeCycleStop;
import dev.voidframework.web.exception.ErrorHandlerException;
import dev.voidframework.web.exception.FilterException;
import dev.voidframework.web.exception.RoutingException;
import dev.voidframework.web.http.converter.StringToBooleanConverter;
import dev.voidframework.web.http.converter.StringToByteConverter;
import dev.voidframework.web.http.converter.StringToCharacterConverter;
import dev.voidframework.web.http.converter.StringToDoubleConverter;
import dev.voidframework.web.http.converter.StringToFloatConverter;
import dev.voidframework.web.http.converter.StringToIntegerConverter;
import dev.voidframework.web.http.converter.StringToLongConverter;
import dev.voidframework.web.http.converter.StringToShortConverter;
import dev.voidframework.web.http.converter.StringToUUIDConverter;
import dev.voidframework.web.http.errorhandler.ErrorHandler;
import dev.voidframework.web.http.filter.Filter;
import dev.voidframework.web.http.routing.AppRoutesDefinition;
import dev.voidframework.web.http.routing.Router;
import dev.voidframework.web.http.routing.RouterPostInitialization;
import dev.voidframework.web.server.http.HttpRequestHandler;
import dev.voidframework.web.server.http.SessionSigner;
import dev.voidframework.web.server.http.UndertowHttpHandler;
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
@BindClass
@Singleton
public class WebServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);

    private static final String CONFIGURATION_KEY_ERROR_HANDLER = "voidframework.web.errorHandler";
    private static final String CONFIGURATION_KEY_GLOBAL_FILTERS = "voidframework.web.globalFilters";
    private static final String CONFIGURATION_KEY_ROUTES = "voidframework.web.routes";
    private static final String CONFIGURATION_KEY_GRACEFUL_STOP_TIMEOUT = "voidframework.web.gracefulStopTimeout";
    private static final String CONFIGURATION_KEY_MAX_REQUEST_BODY_SIZE = "voidframework.web.server.maxBodySize";
    private static final String CONFIGURATION_KEY_LISTEN_PORT = "voidframework.web.server.listenPort";
    private static final String CONFIGURATION_KEY_LISTEN_HOST = "voidframework.web.server.listenHost";
    private static final String CONFIGURATION_KEY_NUMBER_IO_THREADS = "voidframework.web.server.ioThreads";
    private static final String CONFIGURATION_KEY_NUMBER_WORKER_THREADS = "voidframework.web.server.workerThreads";

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
        final String errorHandlerClassName = configuration.getString(CONFIGURATION_KEY_ERROR_HANDLER);
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
        if (this.configuration.hasPath(CONFIGURATION_KEY_GLOBAL_FILTERS)) {
            for (final String filterName : this.configuration.getStringList(CONFIGURATION_KEY_GLOBAL_FILTERS)) {
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
        final Router router = this.injector.getInstance(Router.class);
        if (this.configuration.hasPath(CONFIGURATION_KEY_ROUTES)) {
            this.configuration.getStringList(CONFIGURATION_KEY_ROUTES)
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

        // Post router initialization callback
        if (router instanceof RouterPostInitialization) {
            ((RouterPostInitialization) router).onPostInitialization();
        }

        // Defines the HTTP handler
        final HttpHandler httpHandler = new UndertowHttpHandler(
            this.configuration,
            this.httpRequestHandler,
            new SessionSigner(this.configuration));

        // Configure Undertow
        final Undertow.Builder undertowBuilder = Undertow.builder()
            .setServerOption(
                UndertowOptions.SHUTDOWN_TIMEOUT,
                this.configuration.getInt(CONFIGURATION_KEY_GRACEFUL_STOP_TIMEOUT))
            .setServerOption(
                UndertowOptions.MULTIPART_MAX_ENTITY_SIZE,
                this.configuration.getMemorySize(CONFIGURATION_KEY_MAX_REQUEST_BODY_SIZE).toBytes())
            .setServerOption(
                UndertowOptions.MAX_ENTITY_SIZE,
                this.configuration.getMemorySize(CONFIGURATION_KEY_MAX_REQUEST_BODY_SIZE).toBytes())
            .addHttpListener(
                configuration.getInt(CONFIGURATION_KEY_LISTEN_PORT),
                configuration.getString(CONFIGURATION_KEY_LISTEN_HOST))
            .setHandler(httpServerExchange -> httpServerExchange.dispatch(httpHandler));

        if (this.configuration.hasPath(CONFIGURATION_KEY_NUMBER_IO_THREADS)
            && this.configuration.getInt(CONFIGURATION_KEY_NUMBER_IO_THREADS) > 0) {
            undertowBuilder.setIoThreads(this.configuration.getInt(CONFIGURATION_KEY_NUMBER_IO_THREADS));
        }

        if (this.configuration.hasPath(CONFIGURATION_KEY_NUMBER_WORKER_THREADS)
            && this.configuration.getInt(CONFIGURATION_KEY_NUMBER_WORKER_THREADS) > 0) {
            undertowBuilder.setWorkerThreads(this.configuration.getInt(CONFIGURATION_KEY_NUMBER_WORKER_THREADS));
        }

        this.undertowServer = undertowBuilder.build();

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
    @LifeCycleStop(gracefulStopTimeoutConfigKey = CONFIGURATION_KEY_GRACEFUL_STOP_TIMEOUT)
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
