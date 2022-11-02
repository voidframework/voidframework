package dev.voidframework.web.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.core.bindable.BindClass;
import dev.voidframework.core.conversion.ConverterManager;
import dev.voidframework.core.lang.CUID;
import dev.voidframework.core.lifecycle.LifeCycleStart;
import dev.voidframework.core.lifecycle.LifeCycleStop;
import dev.voidframework.core.utils.ClassResolverUtils;
import dev.voidframework.web.exception.ErrorHandlerException;
import dev.voidframework.web.exception.ExtraWebServerConfigurationException;
import dev.voidframework.web.exception.FilterException;
import dev.voidframework.web.exception.RoutingException;
import dev.voidframework.web.http.HttpMethod;
import dev.voidframework.web.http.converter.StringToBooleanConverter;
import dev.voidframework.web.http.converter.StringToByteConverter;
import dev.voidframework.web.http.converter.StringToCUIDConverter;
import dev.voidframework.web.http.converter.StringToCharacterConverter;
import dev.voidframework.web.http.converter.StringToDoubleConverter;
import dev.voidframework.web.http.converter.StringToFloatConverter;
import dev.voidframework.web.http.converter.StringToIntegerConverter;
import dev.voidframework.web.http.converter.StringToLocaleConverter;
import dev.voidframework.web.http.converter.StringToLongConverter;
import dev.voidframework.web.http.converter.StringToShortConverter;
import dev.voidframework.web.http.converter.StringToUUIDConverter;
import dev.voidframework.web.http.errorhandler.ErrorHandler;
import dev.voidframework.web.http.filter.Filter;
import dev.voidframework.web.http.routing.AppRoutesDefinition;
import dev.voidframework.web.http.routing.Router;
import dev.voidframework.web.http.routing.RouterPostInitialization;
import dev.voidframework.web.server.http.HttpRequestHandler;
import dev.voidframework.web.server.http.HttpWebSocketRequestHandler;
import dev.voidframework.web.server.http.SessionSigner;
import dev.voidframework.web.server.http.UndertowHttpHandler;
import dev.voidframework.web.server.http.UndertowWebSocketCallback;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Web server.
 */
@BindClass
@Singleton
public class WebServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);

    private static final String CONFIGURATION_KEY_ERROR_HANDLER_IMPLEMENTATION = "voidframework.web.errorHandler";
    private static final String CONFIGURATION_KEY_GLOBAL_FILTERS = "voidframework.web.globalFilters";
    private static final String CONFIGURATION_KEY_ROUTES = "voidframework.web.routes";
    private static final String CONFIGURATION_KEY_GRACEFUL_STOP_TIMEOUT = "voidframework.web.gracefulStopTimeout";
    private static final String CONFIGURATION_KEY_IDLE_TIMEOUT = "voidframework.web.server.idleTimeout";
    private static final String CONFIGURATION_KEY_MAX_REQUEST_BODY_SIZE = "voidframework.web.server.maxBodySize";
    private static final String CONFIGURATION_KEY_LISTEN_PORT = "voidframework.web.server.listenPort";
    private static final String CONFIGURATION_KEY_LISTEN_HOST = "voidframework.web.server.listenHost";
    private static final String CONFIGURATION_KEY_NUMBER_IO_THREADS = "voidframework.web.server.ioThreads";
    private static final String CONFIGURATION_KEY_NUMBER_WORKER_THREADS = "voidframework.web.server.workerThreads";
    private static final String CONFIGURATION_EXTRA_WEBSERVER_CONFIGURATION_IMPLEMENTATION = "voidframework.web.server.extraWebServerConfiguration";

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

        // Instantiates the Http request handler
        final ErrorHandler errorHandler = this.instantiateErrorHandler();
        final List<Class<? extends Filter>> globalFilterList = this.retrieveAllGlobalFilters();

        this.httpRequestHandler = new HttpRequestHandler(this.injector, errorHandler, globalFilterList);

        // Built-in converters
        this.registerBuiltInConverters();

        // Load programmatically defined routes
        final Router router = this.loadProgrammaticallyDefinedRoutes();

        // Post router initialization callback
        if (router instanceof RouterPostInitialization) {
            ((RouterPostInitialization) router).onPostInitialization();
        }

        // Build Undertow server
        this.undertowServer = this.createUndertowWebServer(router);

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

    /**
     * Load programmatically defined custom routes.
     *
     * @return The router instance
     */
    private Router loadProgrammaticallyDefinedRoutes() {

        final Router router = this.injector.getInstance(Router.class);
        if (this.configuration.hasPath(CONFIGURATION_KEY_ROUTES)) {
            this.configuration.getStringList(CONFIGURATION_KEY_ROUTES)
                .stream()
                .filter(StringUtils::isNotEmpty)
                .forEach(appRoutesDefinitionClassName -> {
                    final Class<?> abstractRoutesDefinitionClass = ClassResolverUtils.forName(appRoutesDefinitionClassName);
                    if (abstractRoutesDefinitionClass == null) {
                        throw new RoutingException.AppRouteDefinitionLoadFailure(appRoutesDefinitionClassName);
                    }

                    final AppRoutesDefinition appRoutesDefinition = (AppRoutesDefinition) this.injector.getInstance(abstractRoutesDefinitionClass);
                    appRoutesDefinition.defineAppRoutes(router);
                });
        }

        return router;
    }

    /**
     * Retrieves all global filters.
     *
     * @return All global filters
     */
    private List<Class<? extends Filter>> retrieveAllGlobalFilters() {

        final List<Class<? extends Filter>> globalFilterList = new ArrayList<>();
        if (this.configuration.hasPath(CONFIGURATION_KEY_GLOBAL_FILTERS)) {
            for (final String filterName : this.configuration.getStringList(CONFIGURATION_KEY_GLOBAL_FILTERS)) {
                final Class<? extends Filter> filterClass = ClassResolverUtils.forName(filterName);
                if (filterClass == null) {
                    throw new FilterException.LoadFailure(filterName);
                }

                globalFilterList.add(filterClass);
            }
        }

        return globalFilterList;
    }

    /**
     * Instantiates the error handler
     *
     * @return Instantiated error handler
     */
    private ErrorHandler instantiateErrorHandler() {

        final String errorHandlerClassName = configuration.getString(CONFIGURATION_KEY_ERROR_HANDLER_IMPLEMENTATION);
        final Class<?> errorHandlerClass = ClassResolverUtils.forName(errorHandlerClassName);
        if (errorHandlerClass == null) {
            throw new ErrorHandlerException.ClassNotFound(errorHandlerClassName);
        } else if (!ErrorHandler.class.isAssignableFrom(errorHandlerClass)) {
            throw new ErrorHandlerException.InvalidClass(errorHandlerClassName);
        }

        try {
            final ErrorHandler errorHandler = (ErrorHandler) this.injector.getInstance(errorHandlerClass);
            if (errorHandler == null) {
                throw new ErrorHandlerException.CantInstantiate(errorHandlerClassName);
            }

            return errorHandler;
        } catch (final Exception exception) {
            throw new ErrorHandlerException.CantInstantiate(errorHandlerClassName, exception);
        }
    }

    /**
     * Registers built-in converters.
     */
    private void registerBuiltInConverters() {

        final ConverterManager converterManager = this.injector.getInstance(ConverterManager.class);
        converterManager.registerConverter(String.class, Boolean.class, new StringToBooleanConverter());
        converterManager.registerConverter(String.class, Byte.class, new StringToByteConverter());
        converterManager.registerConverter(String.class, CUID.class, new StringToCUIDConverter());
        converterManager.registerConverter(String.class, Character.class, new StringToCharacterConverter());
        converterManager.registerConverter(String.class, Double.class, new StringToDoubleConverter());
        converterManager.registerConverter(String.class, Float.class, new StringToFloatConverter());
        converterManager.registerConverter(String.class, Integer.class, new StringToIntegerConverter());
        converterManager.registerConverter(String.class, Locale.class, new StringToLocaleConverter());
        converterManager.registerConverter(String.class, Long.class, new StringToShortConverter());
        converterManager.registerConverter(String.class, Short.class, new StringToLongConverter());
        converterManager.registerConverter(String.class, UUID.class, new StringToUUIDConverter());
    }

    /**
     * Creates Undertow web server.
     *
     * @return Newly created Undertow web server instance
     */
    private Undertow createUndertowWebServer(final Router router) {

        // Configure Undertow
        final Undertow.Builder undertowBuilder = Undertow.builder()
            .setServerOption(
                UndertowOptions.SHUTDOWN_TIMEOUT,
                this.configuration.getInt(CONFIGURATION_KEY_GRACEFUL_STOP_TIMEOUT))
            .setServerOption(
                UndertowOptions.NO_REQUEST_TIMEOUT,
                (int) this.configuration.getDuration(CONFIGURATION_KEY_IDLE_TIMEOUT, TimeUnit.MILLISECONDS))
            .setServerOption(
                UndertowOptions.MULTIPART_MAX_ENTITY_SIZE,
                this.configuration.getMemorySize(CONFIGURATION_KEY_MAX_REQUEST_BODY_SIZE).toBytes())
            .setServerOption(
                UndertowOptions.MAX_ENTITY_SIZE,
                this.configuration.getMemorySize(CONFIGURATION_KEY_MAX_REQUEST_BODY_SIZE).toBytes())
            .addHttpListener(
                configuration.getInt(CONFIGURATION_KEY_LISTEN_PORT),
                configuration.getString(CONFIGURATION_KEY_LISTEN_HOST));

        if (this.configuration.hasPath(CONFIGURATION_KEY_NUMBER_IO_THREADS)
            && this.configuration.getInt(CONFIGURATION_KEY_NUMBER_IO_THREADS) > 0) {
            undertowBuilder.setIoThreads(this.configuration.getInt(CONFIGURATION_KEY_NUMBER_IO_THREADS));
        }

        if (this.configuration.hasPath(CONFIGURATION_KEY_NUMBER_WORKER_THREADS)
            && this.configuration.getInt(CONFIGURATION_KEY_NUMBER_WORKER_THREADS) > 0) {
            undertowBuilder.setWorkerThreads(this.configuration.getInt(CONFIGURATION_KEY_NUMBER_WORKER_THREADS));
        }

        // Defines handler(s)
        final HttpHandler httpHandler = new UndertowHttpHandler(
            this.configuration,
            this.httpRequestHandler,
            new SessionSigner(this.configuration));

        if (router.getRoutesAsMap().get(HttpMethod.WEBSOCKET) != null) {
            final HttpWebSocketRequestHandler wsIncomingConnHandler = new HttpWebSocketRequestHandler(this.injector);
            final WebSocketConnectionCallback wsCallback = new UndertowWebSocketCallback(wsIncomingConnHandler);
            final HttpHandler wsHandler = new WebSocketProtocolHandshakeHandler(wsCallback, httpHandler);

            undertowBuilder.setHandler(wsHandler);
        } else {
            undertowBuilder.setHandler(httpHandler);
        }

        // Tries to Apply extra configuration
        if (this.configuration.hasPath(CONFIGURATION_EXTRA_WEBSERVER_CONFIGURATION_IMPLEMENTATION)) {
            this.applyExtraWebServerConfiguration(
                undertowBuilder,
                this.configuration.getString(CONFIGURATION_EXTRA_WEBSERVER_CONFIGURATION_IMPLEMENTATION));
        }

        return undertowBuilder.build();
    }

    /**
     * Applies extra configuration to Undertow web server.
     *
     * @param undertowBuilder                      The Undertow web server builder
     * @param extraWebServerConfigurationClassName Class name of the {@link ExtraWebServerConfiguration} implementation to use
     * @see ExtraWebServerConfiguration
     */
    private void applyExtraWebServerConfiguration(final Undertow.Builder undertowBuilder, final String extraWebServerConfigurationClassName) {

        final Class<?> extraWebServerConfigurationClass = ClassResolverUtils.forName(extraWebServerConfigurationClassName);
        if (extraWebServerConfigurationClass == null) {
            throw new ExtraWebServerConfigurationException.ClassNotFound(extraWebServerConfigurationClassName);
        } else if (!ExtraWebServerConfiguration.class.isAssignableFrom(extraWebServerConfigurationClass)) {
            throw new ExtraWebServerConfigurationException.InvalidClass(extraWebServerConfigurationClassName);
        }

        final ExtraWebServerConfiguration extraWebServerConfiguration;
        try {
            extraWebServerConfiguration = (ExtraWebServerConfiguration) this.injector.getInstance(extraWebServerConfigurationClass);
            if (extraWebServerConfiguration == null) {
                throw new ExtraWebServerConfigurationException.CantInstantiate(extraWebServerConfigurationClassName);
            }
        } catch (final Exception exception) {
            throw new ExtraWebServerConfigurationException.CantInstantiate(extraWebServerConfigurationClassName, exception);
        }

        extraWebServerConfiguration.doExtraConfiguration(undertowBuilder);
    }
}
