package dev.voidframework.core.classestoload;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.inject.AbstractModule;
import dev.voidframework.core.bindable.Bindable;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.conversion.TypeConverter;
import dev.voidframework.core.exception.ConversionException;
import dev.voidframework.core.proxyable.Proxyable;
import dev.voidframework.core.utils.ClassResolverUtils;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.ScanResult;
import io.github.classgraph.TypeArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Classpath scanner to fin classes to load.
 *
 * @since 1.0.0
 */
public final class ClassesToLoadScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassesToLoadScanner.class);

    /**
     * Default constructor.
     *
     * @since 1.0.0
     */
    private ClassesToLoadScanner() {
    }

    /**
     * Scan given paths to find classes to bind.
     *
     * @param acceptedScanPaths The locations to scan for classes to bind
     * @param rejectedScanPaths The locations to exclude from the scan
     * @param extraInterfaces   The extra interface for which consider implementations as useful classes to load
     * @return Scan result
     * @since 1.0.0
     */
    public static ScannedClassesToLoad findClassesToLoad(final String[] acceptedScanPaths,
                                                         final String[] rejectedScanPaths,
                                                         final List<String> extraInterfaces) {

        final ScannedClassesToLoad scannedClassesToLoad = new ScannedClassesToLoad(
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>());

        try (final ScanResult scanResult = new ClassGraph()
            .acceptPackages(acceptedScanPaths)
            .rejectPackages(rejectedScanPaths)
            .enableAnnotationInfo()
            .enableMethodInfo()
            .scan()) {

            for (final ClassInfo classInfo : scanResult.getAllClasses()) {

                if (isBindable(classInfo, extraInterfaces)) {
                    scannedClassesToLoad.bindableList().add(classInfo.loadClass(false));
                } else if (classInfo.extendsSuperclass(AbstractModule.class)) {
                    scannedClassesToLoad.moduleList().add(classInfo.loadClass(false));
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
                    final Class<?> sourceClassType = ClassResolverUtils.forName(sourceClassName);
                    if (sourceClassType == null) {
                        throw new ConversionException.InvalidConverter(
                            classInfo.getName(), "Can't retrieve Class<?> from '" + sourceClassName + StringConstants.SIMPLE_QUOTE);
                    }

                    final String targetClassName = typeArgumentList.get(1).getTypeSignature().toString();
                    final Class<?> targetClassType = ClassResolverUtils.forName(targetClassName);
                    if (targetClassType == null) {
                        throw new ConversionException.InvalidConverter(
                            classInfo.getName(), "Can't retrieve Class<?> from '" + targetClassName + StringConstants.SIMPLE_QUOTE);
                    }

                    // Retrieves constructor
                    final MethodInfoList constructorInfoList = classInfo.getConstructorInfo();
                    if (constructorInfoList.isEmpty()) {
                        throw new ConversionException.InvalidConverter(classInfo.getName(), "No constructor found");
                    }

                    final MethodInfo constructorInfo = constructorInfoList.get(0);

                    scannedClassesToLoad.converterInformationList().add(
                        new ConverterInformation(sourceClassType, targetClassType, constructorInfo.loadClassAndGetConstructor().getDeclaringClass()));
                } else if (isProxyable(classInfo)) {
                    scannedClassesToLoad.proxyableList().add(classInfo.loadClass(false));
                }
            }
        }

        return scannedClassesToLoad;
    }

    /**
     * Restore a {@code ScannedClassesToLoad} from the given stream.
     *
     * @param inputStream The stream to use to restore {@code ScannedClassesToLoad}
     * @return Restored {@code ScannedClassesToLoad}
     * @since 1.0.0
     */
    public static ScannedClassesToLoad restoreClassesToLoad(final InputStream inputStream) {

        final Kryo kryo = ClassesToLoadScanner.initializeKryo();

        final Input input = new Input(inputStream);
        final ScannedClassesToLoad scannedClassesToLoad = kryo.readObject(input, ScannedClassesToLoad.class);
        input.close();

        return scannedClassesToLoad;
    }

    /**
     * Persists a {@code ScannedClassesToLoad} object.
     *
     * @param scannedClassesToLoad The {@code ScannedClassesToLoad} object to persist
     * @param outputDirectoryPath  Output directory
     * @since 1.0.0
     */
    public static void persistClassesToLoad(final ScannedClassesToLoad scannedClassesToLoad,
                                            final Path outputDirectoryPath) {

        final Kryo kryo = ClassesToLoadScanner.initializeKryo();

        try {
            final File outputFile = outputDirectoryPath.resolve("classpath.bootstrap").toFile();
            final Output output = new Output(new FileOutputStream(outputFile));
            kryo.writeObject(output, scannedClassesToLoad);
            output.close();
        } catch (final IOException ex) {
            LOGGER.error("Can't save '" + outputDirectoryPath + StringConstants.SIMPLE_QUOTE, ex);
        }
    }

    /**
     * Initializes Kryo.
     *
     * @return Instance of {@code Kryo}
     * @since 1.0.1
     */
    private static Kryo initializeKryo() {

        final Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.register(ArrayList.class);
        kryo.register(Class.class);
        kryo.register(ConverterInformation.class);
        kryo.register(ScannedClassesToLoad.class);

        return kryo;
    }

    /**
     * Checks if current class can be bind.
     *
     * @param classInfo       The current class information
     * @param extraInterfaces The extra interface for which consider implementations as useful classes to load
     * @return {@code true} if can be bind, otherwise {@code false}
     * @since 1.0.0
     */
    private static boolean isBindable(final ClassInfo classInfo,
                                      final List<String> extraInterfaces) {

        if (classInfo.getAnnotationInfo(Bindable.class) != null && !classInfo.isInterfaceOrAnnotation()) {
            return true;
        }

        for (final String interfaceName : extraInterfaces) {
            if (classInfo.implementsInterface(interfaceName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if current interface can be proxy-ed.
     *
     * @param classInfo The current interface information
     * @return {@code true} if can be proxy-ed, otherwise {@code false}
     * @since 1.7.0
     */
    private static boolean isProxyable(final ClassInfo classInfo) {

        return classInfo.getAnnotationInfo(Proxyable.class) != null && classInfo.isInterface();
    }
}
