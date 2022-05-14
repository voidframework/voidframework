package com.voidframework.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.voidframework.core.conversion.Conversion;
import com.voidframework.core.conversion.ConversionProvider;
import com.voidframework.core.helper.VoidFrameworkVersion;
import com.voidframework.core.http.HttpRequestHandler;
import com.voidframework.core.http.impl.DefaultHttpRequestHandler;
import com.voidframework.core.routing.AppRoutesDefinition;
import com.voidframework.core.routing.Router;
import com.voidframework.core.routing.impl.DefaultRouter;
import com.voidframework.core.server.ListenerInformation;
import com.voidframework.core.server.Server;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Application launcher are expected to instantiate and run all parts of an
 * application based on VoidFramework, wiring everything together.
 */
public class ApplicationLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLauncher.class);

    private static final String VOID_FRAMEWORK_BANNER = """
        Booting
         ██╗   ██╗ ██████╗ ██╗██████╗   |  Void Web Framework
         ██║   ██║██╔═══██╗██║██╔══██╗  |  ~~~~~~~~~~~~~~~~~~
         ██║   ██║██║   ██║██║██║  ██║  |
         ╚██╗ ██╔╝██║   ██║██║██║  ██║  |  https://github.com/thibaultmeyer/voidframework
          ╚████╔╝ ╚██████╔╝██║██████╔╝  |
           ╚═══╝   ╚═════╝ ╚═╝╚═════╝   |  Version: {}""";

    private Injector injector;
    private Server server;

    /**
     * Build a new instance.
     */
    public ApplicationLauncher() {
        this.injector = null;
        this.server = null;
    }

    /**
     * Launch Void Framework.
     */
    public void launch() {
        if (this.injector != null) {
            throw new RuntimeException("Application is already launch");
        }

        // Base
        displayBanner();
        System.setProperty("file.encoding", "UTF-8");
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        // Load configuration
        final long startTimeMillis = System.currentTimeMillis();

        LOGGER.info("Fetching configuration...");
        final Config applicationConfiguration = ConfigFactory.defaultApplication(this.getClass().getClassLoader());
        final Config referenceConfiguration = ConfigFactory.defaultReference(this.getClass().getClassLoader()).withOnlyPath("voidframework");
        final Config config = applicationConfiguration.withFallback(referenceConfiguration).resolve();
        LOGGER.info("Configuration fetched with success ({} keys)", config.entrySet().size());

        // Configure core components
        final AbstractModule coreModules = new AbstractModule() {

            @Override
            protected void configure() {
                bind(Config.class).toInstance(config);
                bind(Conversion.class).toProvider(ConversionProvider.class).asEagerSingleton();
                bind(Router.class).to(DefaultRouter.class).asEagerSingleton();
                bind(HttpRequestHandler.class).to(DefaultHttpRequestHandler.class).asEagerSingleton();
            }
        };
        this.injector = Guice.createInjector(Stage.PRODUCTION, Modules.override(coreModules).with(Collections.emptyList()));

        // Configure app components
        if (config.hasPath("voidframework.core.modules")) {
            final List<AbstractModule> appModuleList = new ArrayList<>();
            config.getStringList("voidframework.core.modules")
                .stream()
                .filter(StringUtils::isNotEmpty)
                .forEach(appModuleClassName -> {
                    try {
                        final Class<?> abstractModuleClass = Class.forName(appModuleClassName);
                        final AbstractModule appModule = (AbstractModule) this.injector.getInstance(abstractModuleClass);

                        appModuleList.add(appModule);
                    } catch (final ClassNotFoundException ex) {
                        throw new RuntimeException("Can't find Module '" + appModuleClassName + "'", ex);
                    }
                });

            this.injector = this.injector.createChildInjector(appModuleList);
        }

        // Load app defined routes
        if (config.hasPath("voidframework.core.routes")) {
            final Router router = injector.getInstance(Router.class);
            config.getStringList("voidframework.core.routes")
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

        // Instantiate server implementation to use
        final String serverClassName = config.getString("voidframework.core.serverImplementation");
        try {
            server = (Server) this.injector.getInstance(Class.forName(serverClassName));
        } catch (final ClassNotFoundException ex) {
            throw new RuntimeException("Can't find server implementation '" + serverClassName + "'", ex);
        }

        final List<ListenerInformation> listenerInformationList = server.start();
        for (final ListenerInformation listenerInformation : listenerInformationList) {
            LOGGER.info("Server now listening on {}", listenerInformation);
        }

        // Ready
        final long endTimeMillis = System.currentTimeMillis();
        LOGGER.info("Application started in {}ms", endTimeMillis - startTimeMillis);
    }

    /**
     * Stop VoidFramework.
     */
    private void stop() {
        LOGGER.info("Stopping application...");
        if (server != null) {
            server.onStop();
        }
    }

    /**
     * Display the banner.
     */
    private void displayBanner() {
        String bannerToDisplay = VOID_FRAMEWORK_BANNER;

        try (final InputStream inputStream = this.getClass().getResourceAsStream("/banner.txt")) {
            if (inputStream != null) {
                bannerToDisplay = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (final IOException ignore) {
        }

        if (StringUtils.isNotEmpty(bannerToDisplay)) {
            LOGGER.info(bannerToDisplay, VoidFrameworkVersion.getVersion());
        } else {
            LOGGER.info("Booting application...");
        }
    }
}
