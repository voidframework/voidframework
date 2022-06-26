package dev.voidframework.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.classestoload.ClassesToLoadScanner;
import dev.voidframework.core.classestoload.ConverterInformation;
import dev.voidframework.core.classestoload.ScannedClassesToLoad;
import dev.voidframework.core.conversion.Conversion;
import dev.voidframework.core.conversion.ConverterManager;
import dev.voidframework.core.conversion.TypeConverter;
import dev.voidframework.core.conversion.impl.DefaultConversion;
import dev.voidframework.core.conversion.impl.DefaultConverterManager;
import dev.voidframework.core.exception.AppLauncherException;
import dev.voidframework.core.helper.VoidFrameworkVersion;
import dev.voidframework.core.lifecycle.LifeCycleAnnotationListener;
import dev.voidframework.core.lifecycle.LifeCycleManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application launcher are expected to instantiate and run all parts of an
 * application based on Void Framework, wiring everything together.
 */
public class ApplicationLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLauncher.class);

    private Injector injector;
    private LifeCycleManager lifeCycleManager;

    /**
     * Build a new instance.
     */
    public ApplicationLauncher() {

        this.injector = null;
        this.lifeCycleManager = null;
    }

    /**
     * Launch Void Framework.
     */
    public void launch() {

        if (this.injector != null) {
            throw new AppLauncherException.AlreadyRunning();
        }

        // Base
        System.setProperty("file.encoding", "UTF-8");
        configureShutdownHook();
        displayBanner();

        // Load configuration
        final long startTimeMillis = System.currentTimeMillis();

        LOGGER.info("Fetching configuration");
        final Config applicationConfiguration = ConfigFactory.defaultApplication(this.getClass().getClassLoader());
        final Config referenceConfiguration = ConfigFactory.defaultReference(this.getClass().getClassLoader()).withOnlyPath("voidframework");
        final Config configuration = applicationConfiguration.withFallback(referenceConfiguration).resolve();
        LOGGER.info("Configuration fetched with success ({} keys)", configuration.entrySet().size());

        // Find useful classes to load
        LOGGER.info("Scanning class path");
        final InputStream inputStream = this.getClass().getResourceAsStream("/classpath.bootstrap");
        final ScannedClassesToLoad scannedClassesToLoad;
        if (inputStream != null) {
            scannedClassesToLoad = ClassesToLoadScanner.restoreClassesToLoad(inputStream);
        } else {
            scannedClassesToLoad = ClassesToLoadScanner.findClassesToLoad(
                configuration.getStringList("voidframework.core.acceptedScanPaths").toArray(new String[0]),
                configuration.getStringList("voidframework.core.rejectedScanPaths").toArray(new String[0]),
                configuration.getStringList("voidframework.core.bindExtraInterfaces"));
        }
        LOGGER.info("Found {} useful classes", scannedClassesToLoad.count());

        // Configure core components
        this.lifeCycleManager = new LifeCycleManager(configuration);

        final AbstractModule coreModule = new AbstractModule() {

            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                bind(ConverterManager.class).to(DefaultConverterManager.class).asEagerSingleton();
                bind(Conversion.class).to(DefaultConversion.class).asEagerSingleton();

                bindListener(Matchers.any(), new LifeCycleAnnotationListener(lifeCycleManager));

                requestInjection(lifeCycleManager);
            }
        };

        final AbstractModule scanClassBindModule = new AbstractModule() {

            private final Map<Class<?>, Multibinder<?>> multibinderMap = new HashMap<>();

            @Override
            @SuppressWarnings("unchecked")
            protected void configure() {
                if (configuration.getBoolean("voidframework.core.requireExplicitBindings")) {
                    binder().requireExplicitBindings();
                }

                for (final Class<?> clazz : scannedClassesToLoad.bindableList()) {
                    bind(clazz);

                    for (final Class<?> interfaceClassType : clazz.getInterfaces()) {
                        this.multibinderMap.computeIfAbsent(interfaceClassType,
                            key -> Multibinder.newSetBinder(binder(), interfaceClassType)
                        ).addBinding().to((Class) clazz);
                    }
                }

                if (configuration.getBoolean("voidframework.core.requireExplicitBindings")) {
                    for (final ConverterInformation converterInformation : scannedClassesToLoad.converterInformationList()) {
                        bind(converterInformation.converterTypeClass());
                    }
                }
            }
        };

        // Configure app components
        final List<AbstractModule> appModuleList = new ArrayList<>();
        final List<String> disabledModuleList = configuration.getStringList("voidframework.core.disabledModules");
        for (final Class<?> moduleClass : scannedClassesToLoad.moduleList()) {
            if (disabledModuleList.contains(moduleClass.getName())) {
                // Don't load this module
                continue;
            }

            try {
                AbstractModule appModule;
                try {
                    appModule = (AbstractModule) moduleClass.getDeclaredConstructor().newInstance();
                } catch (final IllegalArgumentException | NoSuchMethodException ignore) {
                    appModule = (AbstractModule) moduleClass.getDeclaredConstructor(Config.class).newInstance(configuration);
                }

                appModuleList.add(appModule);
            } catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                throw new AppLauncherException.ModuleInitFailure(moduleClass, ex);
            }
        }
        LOGGER.info("Modules loaded ({} modules)", appModuleList.size());

        // Create injector
        this.injector = Guice.createInjector(Stage.PRODUCTION, coreModule, Modules.combine(appModuleList), scanClassBindModule);

        // Register detected converters
        LOGGER.info("Registering converters");
        final ConverterManager converterManager = this.injector.getInstance(ConverterManager.class);
        for (final ConverterInformation converterInfo : scannedClassesToLoad.converterInformationList()) {
            final TypeConverter<?, ?> converter = (TypeConverter<?, ?>) injector.getInstance(converterInfo.converterTypeClass());
            converterManager.registerConverter(converterInfo.sourceTypeClass(), converterInfo.targetTypeClass(), converter);
        }
        LOGGER.info("{} converter(s) has been registered", converterManager.count());

        // Execute all registered "start" handlers
        this.lifeCycleManager.startAll();

        // Ready
        final long endTimeMillis = System.currentTimeMillis();
        LOGGER.info("Application started in {}ms", endTimeMillis - startTimeMillis);
    }

    /**
     * Configures the shutdown hook.
     */
    private void configureShutdownHook() {

        final Thread shutdownThread = new Thread(this::stop);
        shutdownThread.setName("Shutdown");

        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    /**
     * Stop VoidFramework.
     */
    private void stop() {

        LOGGER.info("Stopping application");

        // Execute all registered "stop" handlers
        if (this.lifeCycleManager != null) {
            this.lifeCycleManager.stopAll();
        }
    }

    /**
     * Display the banner.
     */
    private void displayBanner() {

        String bannerToDisplay = readFileContent("/banner.txt");
        if (bannerToDisplay == null) {
            bannerToDisplay = readFileContent("/banner.default.txt");
        }

        if (StringUtils.isNotEmpty(bannerToDisplay)) {
            LOGGER.info(bannerToDisplay, VoidFrameworkVersion.getVersion());
        } else {
            LOGGER.info("Booting application");
        }
    }

    /**
     * Returns the content of a file.
     *
     * @param fileName The file name to read
     * @return The file content
     */
    private String readFileContent(final String fileName) {

        try (final InputStream inputStream = this.getClass().getResourceAsStream(fileName)) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();
            }
        } catch (final IOException ignore) {
        }

        return null;
    }
}
