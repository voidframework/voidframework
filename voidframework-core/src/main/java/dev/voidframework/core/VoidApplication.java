package dev.voidframework.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.classestoload.ClassesToLoadScanner;
import dev.voidframework.core.classestoload.ConverterInformation;
import dev.voidframework.core.classestoload.ScannedClassesToLoad;
import dev.voidframework.core.conditionalfeature.ConditionalFeatureVerifier;
import dev.voidframework.core.conversion.ConverterManager;
import dev.voidframework.core.conversion.TypeConverter;
import dev.voidframework.core.exception.AppLauncherException;
import dev.voidframework.core.lifecycle.LifeCycleManager;
import dev.voidframework.core.module.OrderedModule;
import dev.voidframework.core.remoteconfiguration.RemoteConfigurationLoader;
import dev.voidframework.core.utils.VoidFrameworkVersion;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Application launcher are expected to instantiate and run all parts of an
 * application based on Void Framework, wiring everything together.
 *
 * @since 1.1.0
 */
public class VoidApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(VoidApplication.class);

    private Injector injector;
    private LifeCycleManager lifeCycleManager;

    /**
     * Build a new instance.
     *
     * @since 1.1.0
     */
    public VoidApplication() {

        this.injector = null;
        this.lifeCycleManager = null;
    }

    /**
     * Launch Void Framework.
     *
     * @since 1.1.0
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
        final Config localConfiguration = ConfigFactory.load(this.getClass().getClassLoader());
        final Config remoteConfiguration = RemoteConfigurationLoader.processAllProviders(localConfiguration);
        final Config configuration = remoteConfiguration.withFallback(localConfiguration);
        LOGGER.info("Configuration fetched with success ({} keys)", configuration.entrySet().size());

        // Find useful classes to load
        LOGGER.info("Scanning class path");
        final InputStream inputStream = this.getClass().getResourceAsStream("/classpath.bootstrap");
        final ScannedClassesToLoad scannedClassesToLoad;
        if (inputStream != null) {
            scannedClassesToLoad = ClassesToLoadScanner.restoreClassesToLoad(inputStream);
        } else {
            scannedClassesToLoad = ClassesToLoadScanner.findClassesToLoad(
                configuration.getStringList("voidframework.core.acceptedScanPaths"),
                configuration.getStringList("voidframework.core.rejectedScanPaths"),
                configuration.getStringList("voidframework.core.bindExtraInterfaces"));
        }
        LOGGER.info("Found {} useful classes", scannedClassesToLoad.count());

        // Configure components
        final ConditionalFeatureVerifier conditionalFeatureVerifier = new ConditionalFeatureVerifier(configuration);
        this.lifeCycleManager = new LifeCycleManager(configuration);

        final AbstractModule coreModule = new CoreModule(configuration, this.lifeCycleManager);
        final AbstractModule scanClassBindModule = new ScanClassBindModule(configuration, conditionalFeatureVerifier, scannedClassesToLoad);

        // Configure app modules
        final List<Module> appModuleList = new ArrayList<>();
        final List<String> disabledModuleList = configuration.getStringList("voidframework.core.disabledModules");
        for (final Class<?> moduleClassType : scannedClassesToLoad.moduleList()) {
            if (disabledModuleList.contains(moduleClassType.getName()) || conditionalFeatureVerifier.isFeatureDisabled(moduleClassType)) {
                // Don't load this module
                continue;
            }

            try {
                final Module module = this.instantiateModule(configuration, moduleClassType, scannedClassesToLoad);
                appModuleList.add(module);
            } catch (final InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                throw new AppLauncherException.ModuleInitFailure(moduleClassType, ex);
            }
        }

        // Sort all modules per priority
        final Comparator<Module> orderedModuleComparator = Comparator.comparingInt(module -> {
            if (module instanceof final OrderedModule orderedModule) {
                return orderedModule.priority();
            } else {
                return 0;
            }
        });
        appModuleList.sort(orderedModuleComparator);

        // Create injector
        this.injector = Guice.createInjector(Stage.PRODUCTION, coreModule, Modules.combine(appModuleList), scanClassBindModule);
        LOGGER.info("Modules loaded ({} modules)", appModuleList.size());

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
     * Get instance of a specific bind class.
     *
     * @param classType The class type
     * @param <T>       Type of the class
     * @return The class instance
     * @since 1.1.0
     */
    public <T> T getInstance(final Class<T> classType) {

        if (this.injector == null) {
            return null;
        }

        return this.injector.getInstance(classType);
    }

    /**
     * Configures the shutdown hook.
     *
     * @since 1.1.0
     */
    private void configureShutdownHook() {

        final Thread shutdownThread = new Thread(this::stop);
        shutdownThread.setName("Shutdown");

        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    /**
     * Stop VoidFramework.
     *
     * @since 1.1.0
     */
    private void stop() {

        LOGGER.info("Stopping application");

        // Execute all registered "stop" handlers
        if (this.lifeCycleManager != null) {
            this.lifeCycleManager.stopAll();
        }

        LOGGER.info("Application was gracefully terminated");
    }

    /**
     * Instantiates a specific Guice module.
     *
     * @param configuration        The application configuration
     * @param moduleClassType      The Guice module class type
     * @param scannedClassesToLoad The scanned classes
     * @return The instantiated Guice module
     * @throws InvocationTargetException If the underlying constructor throws an exception
     * @throws InstantiationException    If the class that declares the underlying constructor represents an abstract class
     * @throws IllegalAccessException    If this Constructor object is enforcing Java language access control and the underlying constructor is inaccessible
     * @since 1.1.0
     */
    private Module instantiateModule(final Config configuration, final Class<?> moduleClassType, final ScannedClassesToLoad scannedClassesToLoad) throws InvocationTargetException, InstantiationException, IllegalAccessException {

        try {
            // Retrieves the constructor with the most arguments
            final Constructor<?> constructor = Arrays.stream(moduleClassType.getDeclaredConstructors()).max((c1, c2) -> {
                if (c1.getParameterCount() == c2.getParameterCount()) {
                    return 0;
                }

                return c1.getParameterCount() >= c2.getParameterCount() ? 1 : -1;
            }).orElseThrow(() -> new AppLauncherException.ModuleConstructorNotFound(moduleClassType));

            // Build arguments array
            int idx = 0;
            final Object[] argumentArray = new Object[constructor.getParameterCount()];
            for (final Class<?> argumentClassType : constructor.getParameterTypes()) {
                if (argumentClassType == Config.class) {
                    argumentArray[idx] = configuration;
                } else if (argumentClassType == ScannedClassesToLoad.class) {
                    argumentArray[idx] = scannedClassesToLoad;
                } else {
                    argumentArray[idx] = null;
                }

                idx += 1;
            }

            // Creates module instance
            return (Module) constructor.newInstance(argumentArray);
        } catch (final IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new AppLauncherException.ModuleInitFailure(moduleClassType, ex);
        }
    }

    /**
     * Display the banner.
     *
     * @since 1.1.0
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
     * @since 1.1.0
     */
    private String readFileContent(final String fileName) {

        try (final InputStream inputStream = this.getClass().getResourceAsStream(fileName)) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();
            }
        } catch (final IOException ignore) {
            // This exception is not important
        }

        return null;
    }
}
