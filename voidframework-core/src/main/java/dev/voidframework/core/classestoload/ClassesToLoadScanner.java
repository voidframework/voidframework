package dev.voidframework.core.classestoload;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.inject.AbstractModule;
import dev.voidframework.core.bindable.BindClass;
import dev.voidframework.core.conversion.TypeConverter;
import dev.voidframework.core.exception.ConversionException;
import dev.voidframework.core.helper.ClassResolver;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.ScanResult;
import io.github.classgraph.TypeArgument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Classpath scanner to fin classes to load.
 */
public final class ClassesToLoadScanner {

    /**
     * Scan given paths to find classes to bind.
     *
     * @param acceptedScanPaths The locations to scan for classes to bind
     * @param rejectedScanPaths The locations to exclude from the scan
     * @return Scan result
     */
    public static ScannedClassesToLoad findClassesToLoad(final String[] acceptedScanPaths,
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

                    scannedClassesToLoad.converterInformationList().add(
                        new ConverterInformation(sourceClassType, targetClassType, constructorInfo.loadClassAndGetConstructor().getDeclaringClass()));
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
     */
    public static ScannedClassesToLoad restoreClassesToLoad(final InputStream inputStream) {
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
     * Persists a {@code ScannedClassesToLoad} object.
     *
     * @param scannedClassesToLoad The {@code ScannedClassesToLoad} object to persist
     * @param outputDirectoryPath  Output directory
     */
    public static void persistClassesToLoad(final ScannedClassesToLoad scannedClassesToLoad,
                                            final Path outputDirectoryPath) {
        final Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.register(ArrayList.class);
        kryo.register(Class.class);
        kryo.register(ConverterInformation.class);
        kryo.register(ScannedClassesToLoad.class);

        try {
            final File outputFile = outputDirectoryPath.resolve("classpath.bootstrap").toFile();
            final Output output = new Output(new FileOutputStream(outputFile));
            kryo.writeObject(output, scannedClassesToLoad);
            output.close();
        } catch (final IOException exception) {
        }
    }
}
