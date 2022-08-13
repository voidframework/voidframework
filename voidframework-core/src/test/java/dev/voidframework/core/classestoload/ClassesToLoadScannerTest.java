package dev.voidframework.core.classestoload;

import dev.voidframework.core.classestoload.classes.AccountService;
import dev.voidframework.core.classestoload.classes.GuiceModule;
import dev.voidframework.core.classestoload.classes.Person;
import dev.voidframework.core.classestoload.classes.ShippingService;
import dev.voidframework.core.classestoload.classes.StringToIntegerConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ClassesToLoadScannerTest {

    @Test
    public void findClassesToLoad() {

        // Arrange
        final String[] acceptedScanPaths = new String[]{"dev.voidframework.core.classestoload.classes"};
        final String[] rejectedScanPaths = new String[]{"dev.voidframework.core.classestoload.classes.excluded"};
        final List<String> extraInterfaces = List.of("dev.voidframework.core.classestoload.classes.Named");

        // Act
        final ScannedClassesToLoad scannedClassesToLoad = ClassesToLoadScanner.findClassesToLoad(
            acceptedScanPaths,
            rejectedScanPaths,
            extraInterfaces);

        // Assert
        Assertions.assertNotNull(scannedClassesToLoad);
        Assertions.assertEquals(5, scannedClassesToLoad.count());

        final List<ConverterInformation> converterInformationList = scannedClassesToLoad.converterInformationList();
        converterInformationList.sort(Comparator.comparing(convInfo -> convInfo.getClass().getName()));
        Assertions.assertEquals(1, converterInformationList.size());
        Assertions.assertEquals(StringToIntegerConverter.class, converterInformationList.get(0).converterTypeClass());

        final List<Class<?>> bindableList = scannedClassesToLoad.bindableList();
        bindableList.sort(Comparator.comparing(Class::getName));
        Assertions.assertEquals(3, bindableList.size());
        Assertions.assertEquals(AccountService.class, bindableList.get(0));
        Assertions.assertEquals(Person.class, bindableList.get(1));
        Assertions.assertEquals(ShippingService.class, bindableList.get(2));

        final List<Class<?>> moduleList = scannedClassesToLoad.moduleList();
        moduleList.sort(Comparator.comparing(Class::getName));
        Assertions.assertEquals(1, moduleList.size());
        Assertions.assertEquals(GuiceModule.class, moduleList.get(0));
    }

    @Test
    public void persistAndRestoreClassesToLoad() throws IOException {

        // Arrange
        final Path outputPath = Path.of(System.getProperty("java.io.tmpdir"));
        final String[] acceptedScanPaths = new String[]{"dev.voidframework.core.classestoload.classes"};
        final String[] rejectedScanPaths = new String[]{"dev.voidframework.core.classestoload.classes.excluded"};
        final List<String> extraInterfaces = List.of("dev.voidframework.core.classestoload.classes.Named");

        final ScannedClassesToLoad scannedClassesToLoadPersist = ClassesToLoadScanner.findClassesToLoad(
            acceptedScanPaths,
            rejectedScanPaths,
            extraInterfaces);

        // Act
        ClassesToLoadScanner.persistClassesToLoad(scannedClassesToLoadPersist, outputPath);

        ScannedClassesToLoad scannedClassesToLoadRestored;
        try (final InputStream inputStream = Files.newInputStream(outputPath.resolve("classpath.bootstrap"))) {
            scannedClassesToLoadRestored = ClassesToLoadScanner.restoreClassesToLoad(inputStream);
        }

        // Assert
        Assertions.assertNotNull(scannedClassesToLoadRestored);

        Assertions.assertNotNull(scannedClassesToLoadRestored);
        Assertions.assertEquals(5, scannedClassesToLoadRestored.count());

        final List<ConverterInformation> converterInformationList = scannedClassesToLoadRestored.converterInformationList();
        converterInformationList.sort(Comparator.comparing(convInfo -> convInfo.getClass().getName()));
        Assertions.assertEquals(1, converterInformationList.size());
        Assertions.assertEquals(StringToIntegerConverter.class, converterInformationList.get(0).converterTypeClass());

        final List<Class<?>> bindableList = scannedClassesToLoadRestored.bindableList();
        bindableList.sort(Comparator.comparing(Class::getName));
        Assertions.assertEquals(3, bindableList.size());
        Assertions.assertEquals(AccountService.class, bindableList.get(0));
        Assertions.assertEquals(Person.class, bindableList.get(1));
        Assertions.assertEquals(ShippingService.class, bindableList.get(2));

        final List<Class<?>> moduleList = scannedClassesToLoadRestored.moduleList();
        moduleList.sort(Comparator.comparing(Class::getName));
        Assertions.assertEquals(1, moduleList.size());
        Assertions.assertEquals(GuiceModule.class, moduleList.get(0));
    }
}
