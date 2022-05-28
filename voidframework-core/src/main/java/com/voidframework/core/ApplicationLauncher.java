package com.voidframework.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.voidframework.core.bindable.BindClass;
import com.voidframework.core.conversion.Conversion;
import com.voidframework.core.conversion.ConverterManager;
import com.voidframework.core.conversion.TypeConverter;
import com.voidframework.core.conversion.impl.DefaultConversion;
import com.voidframework.core.conversion.impl.DefaultConverterManager;
import com.voidframework.core.exception.ConversionException;
import com.voidframework.core.helper.ClassResolver;
import com.voidframework.core.helper.VoidFrameworkVersion;
import com.voidframework.core.lifecycle.LifeCycleAnnotationListener;
import com.voidframework.core.lifecycle.LifeCycleManager;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.ScanResult;
import io.github.classgraph.TypeArgument;
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

    private static final String VOID_FRAMEWORK_BANNER = """
        Booting
         ██╗   ██╗ ██████╗ ██╗██████╗   |  Void Web Framework
         ██║   ██║██╔═══██╗██║██╔══██╗  |  ~~~~~~~~~~~~~~~~~~
         ██║   ██║██║   ██║██║██║  ██║  |
         ╚██╗ ██╔╝██║   ██║██║██║  ██║  |  https://github.com/thibaultmeyer/voidframework
          ╚████╔╝ ╚██████╔╝██║██████╔╝  |
           ╚═══╝   ╚═════╝ ╚═╝╚═════╝   |  Version: {}""";

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
            throw new RuntimeException("Application is already launch");
        }

        // Base
        System.setProperty("file.encoding", "UTF-8");
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        displayBanner();

        // Load configuration
        final long startTimeMillis = System.currentTimeMillis();

        LOGGER.info("Fetching configuration");
        final Config applicationConfiguration = ConfigFactory.defaultApplication(this.getClass().getClassLoader());
        final Config referenceConfiguration = ConfigFactory.defaultReference(this.getClass().getClassLoader()).withOnlyPath("voidframework");
        final Config config = applicationConfiguration.withFallback(referenceConfiguration).resolve();
        LOGGER.info("Configuration fetched with success ({} keys)", config.entrySet().size());

        // Scan all classpath to find useful classes
        LOGGER.info("Scanning class path");
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final List<Class<?>> classList = new ArrayList<>();
        final List<ConverterInformation> converterInfoList = new ArrayList<>();
        try (final ScanResult scanResult = new ClassGraph()
            .acceptPackages(config.getStringList("voidframework.core.acceptedScanPaths").toArray(new String[0]))
            .rejectPackages(config.getStringList("voidframework.core.rejectedScanPaths").toArray(new String[0]))
            .addClassLoader(classLoader)
            .enableAnnotationInfo()
            .enableMethodInfo()
            .scan()) {

            for (final ClassInfo classInfo : scanResult.getAllClasses()) {

                if (classInfo.getAnnotationInfo(BindClass.class) != null && !classInfo.isInterfaceOrAnnotation()) {
                    classList.add(classInfo.loadClass(false));
                } else if (classInfo.implementsInterface(TypeConverter.class)) {
                    // Determine source class and target class
                    final List<TypeArgument> typeArgumentList = classInfo.getTypeSignature().getSuperinterfaceSignatures()
                        .get(0)
                        .getTypeArguments();
                    if (typeArgumentList.size() != 2) {
                        // Technically not possible, but you might as well be 100% sure
                        throw new ConversionException.InvalidConverter(classInfo.getName(), "Bad number of type parameter");
                    }

                    final String sourceClassName = typeArgumentList.get(0).getTypeSignature().toString();
                    final Class<?> sourceClassType = ClassResolver.forName(sourceClassName, classLoader);
                    if (sourceClassType == null) {
                        throw new ConversionException.InvalidConverter(
                            classInfo.getName(), "Can't retrieve Class<?> from '" + sourceClassName + "'");
                    }

                    final String targetClassName = typeArgumentList.get(1).getTypeSignature().toString();
                    final Class<?> targetClassType = ClassResolver.forName(targetClassName, classLoader);
                    if (targetClassType == null) {
                        throw new ConversionException.InvalidConverter(
                            classInfo.getName(), "Can't retrieve Class<?> from '" + targetClassName + "'");
                    }

                    // Retrieves constructor
                    final MethodInfoList constructorInfoList = classInfo.getConstructorInfo();
                    if (constructorInfoList.isEmpty()) {
                        throw new ConversionException.InvalidConverter(classInfo.getName(), "No constructor found");
                    }

                    final MethodInfo constructorInfo = constructorInfoList.get(0);

                    converterInfoList.add(
                        new ConverterInformation(sourceClassType, targetClassType, constructorInfo.loadClassAndGetConstructor().getDeclaringClass()));
                }
            }
        }
        LOGGER.info("Found {} useful classes", classList.size());

        // Configure core components
        this.lifeCycleManager = new LifeCycleManager(config);

        final AbstractModule coreModule = new AbstractModule() {

            @Override
            protected void configure() {
                bind(Config.class).toInstance(config);
                bind(ConverterManager.class).to(DefaultConverterManager.class).asEagerSingleton();
                bind(Conversion.class).to(DefaultConversion.class).asEagerSingleton();

                bindListener(Matchers.any(), new LifeCycleAnnotationListener(lifeCycleManager));
            }
        };

        final AbstractModule scanClassBindModule = new AbstractModule() {

            private final Map<Class<?>, Multibinder<?>> multibinderMap = new HashMap<>();

            @Override
            @SuppressWarnings("unchecked")
            protected void configure() {
                for (final Class<?> clazz : classList) {
                    bind(clazz).asEagerSingleton();

                    // TODO: Add a configuration key to enable/disable this feature
                    for (final Class<?> interfaceClassType : clazz.getInterfaces()) {
                        this.multibinderMap.computeIfAbsent(interfaceClassType,
                            key -> Multibinder.newSetBinder(binder(), interfaceClassType)
                        ).addBinding().to((Class) clazz);
                    }
                }
            }
        };

        // Configure app components
        final List<AbstractModule> appModuleList = new ArrayList<>();
        if (config.hasPath("voidframework.core.enabledModules")) {
            final List<String> disabledModuleList = config.getStringList("voidframework.core.disabledModules");
            LOGGER.info("Loading modules");
            config.getStringList("voidframework.core.enabledModules")
                .stream()
                .filter(StringUtils::isNotEmpty)
                .forEach(appModuleClassName -> {
                    try {
                        final Class<?> abstractModuleClass = Class.forName(appModuleClassName);
                        if (disabledModuleList.contains(abstractModuleClass.getName())) {
                            // Don't load this module
                            return;
                        }

                        AbstractModule appModule;
                        try {
                            appModule = (AbstractModule) abstractModuleClass.getDeclaredConstructor().newInstance();
                        } catch (final IllegalArgumentException | NoSuchMethodException ignore) {
                            appModule = (AbstractModule) abstractModuleClass.getDeclaredConstructor(Config.class).newInstance(config);
                        }

                        appModuleList.add(appModule);
                    } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                                   InvocationTargetException ex) {
                        throw new RuntimeException("Can't find Module '" + appModuleClassName + "'", ex);
                    }
                });
            LOGGER.info("Modules loaded ({} modules)", appModuleList.size());
        }

        // Create injector
        this.injector = Guice.createInjector(Stage.PRODUCTION, coreModule, Modules.combine(appModuleList), scanClassBindModule);

        // Register detected converters
        LOGGER.info("Registering converters");
        final ConverterManager converterManager = this.injector.getInstance(ConverterManager.class);
        for (final ConverterInformation converterInfo : converterInfoList) {
            final TypeConverter<?, ?> converter = (TypeConverter<?, ?>) injector.getInstance(converterInfo.converterTypeClass);
            converterManager.registerConverter(converterInfo.sourceTypeClass, converterInfo.targetTypeClass, converter);
        }
        LOGGER.info("{} converter(s) has been registered", converterManager.count());

        // Execute all registered "start" handlers
        this.lifeCycleManager.startAll();

        // Ready
        final long endTimeMillis = System.currentTimeMillis();
        LOGGER.info("Application started in {}ms", endTimeMillis - startTimeMillis);
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

    private record ConverterInformation(Class<?> sourceTypeClass,
                                        Class<?> targetTypeClass,
                                        Class<?> converterTypeClass) {
    }
}
