package com.voidframework.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Version utils.
 */
public class VersionUtils {

    private static final String DEFAULT_VERSION_NUMBER = "0.0.0";
    private static String versionCache = null;

    /**
     * Retrieves the framework version.
     *
     * @return The version number "x.y.z"
     */
    public static String getVersion() {
        if (versionCache == null) {
            try (final InputStream fis = VersionUtils.class.getResourceAsStream("/voidframework.version")) {
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
