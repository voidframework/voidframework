package dev.voidframework.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.bindable.BindClass;
import dev.voidframework.core.conversion.Conversion;
import dev.voidframework.core.conversion.ConverterManager;
import dev.voidframework.core.conversion.TypeConverter;
import dev.voidframework.core.conversion.impl.DefaultConversion;
import dev.voidframework.core.conversion.impl.DefaultConverterManager;
import dev.voidframework.core.exception.ConversionException;
import dev.voidframework.core.helper.ClassResolver;
import dev.voidframework.core.helper.VoidFrameworkVersion;
import dev.voidframework.core.lifecycle.LifeCycleAnnotationListener;
import dev.voidframework.core.lifecycle.LifeCycleManager;
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
        final Config configuration = applicationConfiguration.withFallback(referenceConfiguration).resolve();
        LOGGER.info("Configuration fetched with success ({} keys)", configuration.entrySet().size());

        // Find useful classes to load
        LOGGER.info("Scanning class path");
        final InputStream inputStream = this.getClass().getResourceAsStream("/classpath.bootstrap");
        final ScannedClassesToLoad scannedClassesToLoad;
        if (inputStream != null) {
            scannedClassesToLoad = this.restoreClassesToLoad(inputStream);
        } else {
            scannedClassesToLoad = this.findClassesToLoad(
                configuration.getStringList("voidframework.core.acceptedScanPaths").toArray(new String[0]),
                configuration.getStringList("voidframework.core.rejectedScanPaths").toArray(new String[0]));
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
            }
        };

        final AbstractModule scanClassBindModule = new AbstractModule() {

            private final Map<Class<?>, Multibinder<?>> multibinderMap = new HashMap<>();

            @Override
            @SuppressWarnings("unchecked")
            protected void configure() {
                for (final Class<?> clazz : scannedClassesToLoad.bindableList) {
                    bind(clazz);

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
        final List<String> disabledModuleList = configuration.getStringList("voidframework.core.disabledModules");
        for (final Class<?> moduleClass : scannedClassesToLoad.moduleList) {
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
                throw new RuntimeException("Can't find Module '" + moduleClass + "'", ex);
            }
        }
        LOGGER.info("Modules loaded ({} modules)", appModuleList.size());

        // Create injector
        this.injector = Guice.createInjector(Stage.PRODUCTION, coreModule, Modules.combine(appModuleList), scanClassBindModule);

        // Register detected converters
        LOGGER.info("Registering converters");
        final ConverterManager converterManager = this.injector.getInstance(ConverterManager.class);
        for (final ConverterInformation converterInfo : scannedClassesToLoad.converterInformationList) {
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
     * Scan given paths to find classes to bind.
     *
     * @param acceptedScanPaths The locations to scan for classes to bind
     * @param rejectedScanPaths The locations to exclude from the scan
     * @return Scan result
     */
    private ScannedClassesToLoad findClassesToLoad(final String[] acceptedScanPaths,
                                                   final String[] rejectedScanPaths) {

        final ScannedClassesToLoad scannedClassesToLoad = new ScannedClassesToLoad(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        try (final ScanResult scanResult = new ClassGraph()
            .acceptPackages(acceptedScanPaths)
            .rejectPackages(rejectedScanPaths)
            .enableAnnotationInfo()
            .enableMethodInfo()
            .scan()) {

            for (final ClassInfo classInfo : scanResult.getAllClasses()) {

                if (classInfo.getAnnotationInfo(BindClass.class) != null && !classInfo.isInterfaceOrAnnotation()) {
                    scannedClassesToLoad.bindableList.add(classInfo.loadClass(false));
                } else if (classInfo.extendsSuperclass(AbstractModule.class)) {
                    scannedClassesToLoad.moduleList.add(classInfo.loadClass(false));
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
                    final Class<?> sourceClassType = ClassResolver.forName(sourceClassName);
                    if (sourceClassType == null) {
                        throw new ConversionException.InvalidConverter(
                            classInfo.getName(), "Can't retrieve Class<?> from '" + sourceClassName + "'");
                    }

                    final String targetClassName = typeArgumentList.get(1).getTypeSignature().toString();
                    final Class<?> targetClassType = ClassResolver.forName(targetClassName);
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

                    scannedClassesToLoad.converterInformationList.add(
                        new ConverterInformation(sourceClassType, targetClassType, constructorInfo.loadClassAndGetConstructor().getDeclaringClass()));
                }
            }
        }

        return scannedClassesToLoad;
    }

    /**
     * Restore {@code ScannedClassesToLoad} from the given stream.
     *
     * @param inputStream The stream to use to restore {@code ScannedClassesToLoad}
     * @return Restored {@code ScannedClassesToLoad}
     */
    private ScannedClassesToLoad restoreClassesToLoad(final InputStream inputStream) {
        final Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.register(ArrayList.class);
        kryo.register(Class.class);
        kryo.register(ConverterInformation.class);
        kryo.register(ScannedClassesToLoad.class);

        final Input input = new Input(inputStream);
        final ScannedClassesToLoad scannedClassesToLoad = kryo.readObject(input, ScannedClassesToLoad.class);
        input.close();

        return scannedClassesToLoad;
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

    /**
     * Scanned classes to load into Guice.
     *
     * @param moduleList               The module classes list
     * @param bindableList             The bindable classes list
     * @param converterInformationList The converter information list
     */
    private record ScannedClassesToLoad(List<Class<?>> moduleList,
                                        List<Class<?>> bindableList,
                                        List<ConverterInformation> converterInformationList) {

        /**
         * Returns the number of scanned classes.
         *
         * @return The number of scanned classes
         */
        public int count() {
            return moduleList.size() + bindableList.size() + converterInformationList.size();
        }
    }

    /**
     * Converter information.
     *
     * @param sourceTypeClass    The source type class
     * @param targetTypeClass    The target type class
     * @param converterTypeClass The converter type class
     */
    private record ConverterInformation(Class<?> sourceTypeClass,
                                        Class<?> targetTypeClass,
                                        Class<?> converterTypeClass) {
    }
}
