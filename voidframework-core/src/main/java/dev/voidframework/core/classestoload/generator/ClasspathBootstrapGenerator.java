package dev.voidframework.core.classestoload.generator;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.classestoload.ClassesToLoadScanner;
import dev.voidframework.core.classestoload.ScannedClassesToLoad;

import java.nio.file.Paths;

/**
 * Generates "classpath.bootstrap" file.
 */
public final class ClasspathBootstrapGenerator {

    /**
     * Main entry of the generator (called by Maven).
     *
     * @param args The arguments
     */
    public static void main(final String[] args) {

        if (args.length == 0) {
            throw new RuntimeException("Output directory must be provided!");
        }

        // Configuration
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Config applicationConfiguration = ConfigFactory.defaultApplication(classLoader);
        final Config referenceConfiguration = ConfigFactory.defaultReference(classLoader).withOnlyPath("voidframework");
        final Config configuration = applicationConfiguration.withFallback(referenceConfiguration).resolve();

        // Scan classpath
        final ScannedClassesToLoad scannedClassesToLoad = ClassesToLoadScanner.findClassesToLoad(
            configuration.getStringList("voidframework.core.acceptedScanPaths").toArray(new String[0]),
            configuration.getStringList("voidframework.core.rejectedScanPaths").toArray(new String[0]),
            configuration.getStringList("voidframework.core.bindExtraInterfaces"));

        // Create "classpath.bootstrap" file
        ClassesToLoadScanner.persistClassesToLoad(scannedClassesToLoad, Paths.get(args[0]));
    }
}
