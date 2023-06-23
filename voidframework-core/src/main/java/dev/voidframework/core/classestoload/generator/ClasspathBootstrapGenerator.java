package dev.voidframework.core.classestoload.generator;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.classestoload.ClassesToLoadScanner;
import dev.voidframework.core.classestoload.ScannedClassesToLoad;
import dev.voidframework.core.exception.ClasspathBootstrapGeneratorException;

import java.nio.file.Paths;
import java.util.List;

/**
 * Generates "classpath.bootstrap" file.
 *
 * @since 1.0.0
 */
public final class ClasspathBootstrapGenerator {

    /**
     * Main entry of the generator (called by Maven).
     *
     * @param args The arguments
     * @since 1.0.0
     */
    public static void main(final String[] args) {

        if (args.length == 0) {
            throw new ClasspathBootstrapGeneratorException.MissingProgramArgument("Output directory must be provided!");
        }

        // Configuration
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Config applicationConfiguration = ConfigFactory.defaultApplication(classLoader);
        final Config referenceConfiguration = ConfigFactory.defaultReference(classLoader).withOnlyPath("voidframework");
        final Config configuration = applicationConfiguration.withFallback(referenceConfiguration).resolve();

        // Scan classpath
        final ScannedClassesToLoad scannedClassesToLoad = ClassesToLoadScanner.findClassesToLoad(
            resolveConfigAsStringList(configuration, "voidframework.core.acceptedScanPaths"),
            resolveConfigAsStringList(configuration, "voidframework.core.rejectedScanPaths"),
            resolveConfigAsStringList(configuration, "voidframework.core.bindExtraInterfaces"));

        // Create "classpath.bootstrap" file
        ClassesToLoadScanner.persistClassesToLoad(scannedClassesToLoad, Paths.get(args[0]));
    }

    /**
     * Resolves a configuration value as a list of String, even if the configuration is a simple String.
     *
     * @param configuration The application configuration
     * @param path          The configuration value path
     * @return A list of String
     * @since 1.0.0
     */
    private static List<String> resolveConfigAsStringList(final Config configuration, final String path) {

        try {
            return configuration.getStringList(path);
        } catch (final ConfigException.WrongType ignore) {
            return List.of(configuration.getString(path));
        }
    }
}
