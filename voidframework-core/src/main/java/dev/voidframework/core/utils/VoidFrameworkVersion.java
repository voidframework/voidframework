package dev.voidframework.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility to retrieve Void Framework version.
 *
 * @since 1.0.0
 */
public final class VoidFrameworkVersion {

    private static final String DEFAULT_VERSION_NUMBER = "0.0.0";
    private static String versionCache = null;

    /**
     * Default constructor.
     *
     * @since 1.0.0
     */
    private VoidFrameworkVersion() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Retrieves the framework version.
     *
     * @return The version number "x.y.z"
     * @since 1.0.0
     */
    public static String getVersion() {

        if (versionCache == null) {
            try (final InputStream fis = VoidFrameworkVersion.class.getResourceAsStream("/voidframework.version")) {
                final Properties properties = new Properties();
                properties.load(fis);

                versionCache = (String) properties.getOrDefault("MAVEN_PROJECT_VERSION", DEFAULT_VERSION_NUMBER);
            } catch (final IOException | NullPointerException ignore) {
                versionCache = DEFAULT_VERSION_NUMBER;
            }
        }

        return versionCache;
    }
}
