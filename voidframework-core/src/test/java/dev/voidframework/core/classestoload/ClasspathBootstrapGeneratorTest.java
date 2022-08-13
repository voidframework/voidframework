package dev.voidframework.core.classestoload;

import dev.voidframework.core.classestoload.generator.ClasspathBootstrapGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.nio.file.Path;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ClasspathBootstrapGeneratorTest {

    @Test
    public void classpathBootstrapGenerator() {

        // Arrange
        final Path outputPath = Path.of(System.getProperty("java.io.tmpdir"));
        final String[] args = new String[]{outputPath.toString()};

        System.setProperty("voidframework.core.acceptedScanPaths", "dev.voidframework.core.classestoload.classes");
        System.setProperty("voidframework.core.rejectedScanPaths", "dev.voidframework.core.classestoload.classes.excluded");
        System.setProperty("voidframework.core.bindExtraInterfaces", "dev.voidframework.core.classestoload.classes.Named");

        // Act
        ClasspathBootstrapGenerator.main(args);

        // Assert
        final File outputFile = outputPath.resolve("classpath.bootstrap").toFile();
        Assertions.assertTrue(outputFile.exists());
        Assertions.assertTrue(outputFile.length() > 300);
    }

    @Test
    public void classpathBootstrapGeneratorEmptyArgs() {

        // Arrange
        final String[] args = new String[]{};

        // Act
        final RuntimeException exception = Assertions.assertThrows(
            RuntimeException.class,
            () -> ClasspathBootstrapGenerator.main(args));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("Output directory must be provided!", exception.getMessage());
    }
}
