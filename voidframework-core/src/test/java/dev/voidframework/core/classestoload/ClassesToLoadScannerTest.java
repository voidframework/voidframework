package dev.voidframework.core.classestoload;

import dev.voidframework.core.classestoload.classes.AccountService;
import dev.voidframework.core.classestoload.classes.GuiceModule;
import dev.voidframework.core.classestoload.classes.Named;
import dev.voidframework.core.classestoload.classes.Person;
import dev.voidframework.core.classestoload.classes.ProxInterface;
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
import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class ClassesToLoadScannerTest {

    @Test
    void findClassesToLoad() {

        // Arrange
        final List<String> acceptedScanPathList = List.of("dev.voidframework.core.classestoload.classes");
        final List<String> rejectedScanPathList = List.of("dev.voidframework.core.classestoload.classes.excluded");
        final List<String> extraInterfaceList = List.of("dev.voidframework.core.classestoload.classes.Named");

        // Act
        final ScannedClassesToLoad scannedClassesToLoad = ClassesToLoadScanner.findClassesToLoad(
            acceptedScanPathList,
            rejectedScanPathList,
            extraInterfaceList);

        // Assert
        Assertions.assertNotNull(scannedClassesToLoad);
        Assertions.assertEquals(6, scannedClassesToLoad.count());

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

        final List<Class<?>> proxyableList = scannedClassesToLoad.proxyableList();
        proxyableList.sort(Comparator.comparing(Class::getName));
        Assertions.assertEquals(1, proxyableList.size());
        Assertions.assertEquals(ProxInterface.class, proxyableList.get(0));

        final Map<Class<?>, Integer> interfaceImplementationCountMap = scannedClassesToLoad.interfaceImplementationCountMap();
        Assertions.assertEquals(1, interfaceImplementationCountMap.size());
        Assertions.assertEquals(1, interfaceImplementationCountMap.get(Named.class));
    }

    @Test
    void persistAndRestoreClassesToLoad() throws IOException {

        // Arrange
        final Path outputPath = Path.of(System.getProperty("java.io.tmpdir"));
        final List<String> acceptedScanPathList = List.of("dev.voidframework.core.classestoload.classes");
        final List<String> rejectedScanPathList = List.of("dev.voidframework.core.classestoload.classes.excluded");
        final List<String> extraInterfaceList = List.of("dev.voidframework.core.classestoload.classes.Named");

        final ScannedClassesToLoad scannedClassesToLoadPersist = ClassesToLoadScanner.findClassesToLoad(
            acceptedScanPathList,
            rejectedScanPathList,
            extraInterfaceList);

        // Act
        ClassesToLoadScanner.persistClassesToLoad(scannedClassesToLoadPersist, outputPath);

        ScannedClassesToLoad scannedClassesToLoadRestored;
        try (final InputStream inputStream = Files.newInputStream(outputPath.resolve("classpath.bootstrap"))) {
            scannedClassesToLoadRestored = ClassesToLoadScanner.restoreClassesToLoad(inputStream);
        }

        // Assert
        Assertions.assertNotNull(scannedClassesToLoadRestored);

        Assertions.assertNotNull(scannedClassesToLoadRestored);
        Assertions.assertEquals(6, scannedClassesToLoadRestored.count());

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

        final List<Class<?>> proxyableList = scannedClassesToLoadRestored.proxyableList();
        proxyableList.sort(Comparator.comparing(Class::getName));
        Assertions.assertEquals(1, proxyableList.size());
        Assertions.assertEquals(ProxInterface.class, proxyableList.get(0));
    }
}
