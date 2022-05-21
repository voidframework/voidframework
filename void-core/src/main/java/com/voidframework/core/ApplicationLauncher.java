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
import com.voidframework.core.conversion.ConverterManager;
import com.voidframework.core.conversion.impl.DefaultConverterManager;
import com.voidframework.core.daemon.Daemon;
import com.voidframework.core.helper.VoidFrameworkVersion;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private final List<Daemon> daemonList;
    private Injector injector;

    /**
     * Build a new instance.
     */
    public ApplicationLauncher() {
        this.daemonList = new ArrayList<>();
        this.injector = null;
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

        LOGGER.info("Fetching configuration");
        final Config applicationConfiguration = ConfigFactory.defaultApplication(this.getClass().getClassLoader());
        final Config referenceConfiguration = ConfigFactory.defaultReference(this.getClass().getClassLoader()).withOnlyPath("voidframework");
        final Config config = applicationConfiguration.withFallback(referenceConfiguration).resolve();
        LOGGER.info("Configuration fetched with success ({} keys)", config.entrySet().size());

        // Configure core components
        final AbstractModule coreModules = new AbstractModule() {

            @Override
            protected void configure() {
                bind(Config.class).toInstance(config);
                bind(ConverterManager.class).to(DefaultConverterManager.class).asEagerSingleton();
                bind(Conversion.class).toProvider(ConversionProvider.class).asEagerSingleton();
            }
        };
        this.injector = Guice.createInjector(Stage.PRODUCTION, Modules.override(coreModules).with(Collections.emptyList()));

        // Instantiate daemons
        final List<String> daemonClassNameList = config.getStringList("voidframework.core.daemons");
        try {
            for (final String daemonClassName : daemonClassNameList) {
                final Daemon daemon = (Daemon) this.injector.getInstance(Class.forName(daemonClassName));
                if (daemon != null) {
                    this.injector = this.injector.createChildInjector(daemon.getModule());
                    this.daemonList.add(daemon);
                }
            }
        } catch (final ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        LOGGER.info("Daemons initialized with success ({} daemons)", this.daemonList.size());

        // Configure app components
        if (config.hasPath("voidframework.core.modules")) {
            LOGGER.info("Loading modules");
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
            LOGGER.info("Modules loaded ({} modules)", appModuleList.size());
        }

        // Start all daemons
        LOGGER.info("Starting all daemons");
        this.daemonList.stream().sorted(Comparator.comparingInt(Daemon::getPriority)).forEach(daemon -> {
            daemon.configure(config, injector);
            daemon.run();
        });

        // Ready
        final long endTimeMillis = System.currentTimeMillis();
        LOGGER.info("Application started in {}ms", endTimeMillis - startTimeMillis);
    }

    /**
     * Stop VoidFramework.
     */
    private void stop() {
        LOGGER.info("Stopping application");

        // Stop all daemons
        final List<Daemon> daemonToStopList = this.daemonList
            .stream()
            .sorted(Comparator.comparingInt(Daemon::getPriority).reversed())
            .toList();

        for (final Daemon daemonToStop : daemonToStopList) {
            final String daemonName = daemonToStop.getClass().getSimpleName();
            LOGGER.info("Stopping '{}'", daemonName);

            try {
                final Thread thread = new Thread(daemonToStop::gracefulStop);

                final long start = System.currentTimeMillis();
                thread.start();
                thread.join(daemonToStop.getGracefulStopTimeout());
                final long end = System.currentTimeMillis();

                LOGGER.info("Daemon '{}' gracefully stopped in {}ms", daemonName, end - start);
            } catch (final InterruptedException e) {
                LOGGER.info("Daemon '{}' stopped (force)", daemonName);
            }
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
            LOGGER.info("Booting application");
        }
    }
}
