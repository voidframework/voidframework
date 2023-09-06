package dev.voidframework.core.utils;

import com.typesafe.config.Config;
import dev.voidframework.core.constant.StringConstants;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Utility methods to use with application configuration.
 *
 * @since 1.9.0
 */
public final class ConfigurationUtils {

    /**
     * Default constructor.
     *
     * @since 1.9.0
     */
    private ConfigurationUtils() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Gets all root paths for the given path expression.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @return All base paths
     * @since 1.9.0
     */
    public static Set<String> getAllRootLevelPaths(final Config configuration, final String path) {

        if (!configuration.hasPathOrNull(path)) {
            new HashSet<>();
        }

        return configuration.getConfig(path).entrySet()
            .stream()
            .map(Map.Entry::getKey)
            .map(key -> {
                if (key.contains(StringConstants.DOT)) {
                    return key.substring(0, key.indexOf(StringConstants.DOT));
                } else {
                    return key;
                }
            })
            .collect(Collectors.toSet());
    }

    /**
     * Gets the boolean at the given path.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param defaultValue  The default value returned if path does not exist or value is null
     * @return The boolean value at the requested path
     * @since 1.9.0
     */
    public static boolean getBooleanOrDefault(final Config configuration, final String path, final boolean defaultValue) {

        if (configuration.hasPath(path)) {
            return configuration.getBoolean(path);
        }

        return defaultValue;
    }

    /**
     * Gets the boolean at the given path.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param fallbackPath  Path expression to use as fallback
     * @return The boolean value at the requested path
     * @since 1.9.0
     */
    public static boolean getBooleanOrFallback(final Config configuration, final String path, final String fallbackPath) {

        if (configuration.hasPath(path)) {
            return configuration.getBoolean(path);
        }

        return configuration.getBoolean(fallbackPath);
    }

    /**
     * Gets a value as a size in bytes (parses special strings like "128M").
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param defaultValue  The default value returned if path does not exist or value is null
     * @return The value at the requested path, in bytes
     * @since 1.9.0
     */
    public static long getBytesOrDefault(final Config configuration, final String path, final long defaultValue) {

        if (configuration.hasPath(path)) {
            return configuration.getBytes(path);
        }

        return defaultValue;
    }

    /**
     * Gets a value as a size in bytes (parses special strings like "128M").
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param fallbackPath  Path expression to use as fallback
     * @return The value at the requested path, in bytes
     * @since 1.9.0
     */
    public static long getBytesOrFallback(final Config configuration, final String path, final String fallbackPath) {

        if (configuration.hasPath(path)) {
            return configuration.getBytes(path);
        }

        return configuration.getBytes(fallbackPath);
    }

    /**
     * Gets a value as a Duration.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param defaultValue  The default value returned if path does not exist or value is null
     * @return The duration value at the requested path
     * @since 1.9.0
     */
    public static Duration getDurationOrDefault(final Config configuration, final String path, final Duration defaultValue) {

        if (configuration.hasPath(path)) {
            return configuration.getDuration(path);
        }

        return defaultValue;
    }

    /**
     * Gets a value as a Duration.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param fallbackPath  Path expression to use as fallback
     * @return The duration value at the requested path
     * @since 1.9.0
     */
    public static Duration getDurationOrFallback(final Config configuration, final String path, final String fallbackPath) {

        if (configuration.hasPath(path)) {
            return configuration.getDuration(path);
        }

        return configuration.getDuration(fallbackPath);
    }

    /**
     * Gets a value as a Duration.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param unit          Convert the return value to this time unit
     * @param defaultValue  The default value returned if path does not exist or value is null
     * @return The Duration value at the requested path
     * @since 1.9.0
     */
    public static long getDurationOrDefault(final Config configuration, final String path, final TimeUnit unit, final long defaultValue) {

        if (configuration.hasPath(path)) {
            return configuration.getDuration(path, unit);
        }

        return defaultValue;
    }

    /**
     * Gets a value as a Duration.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param unit          Convert the return value to this time unit
     * @param fallbackPath  Path expression to use as fallback
     * @return The Duration value at the requested path
     * @since 1.9.0
     */
    public static long getDurationOrFallback(final Config configuration, final String path, final TimeUnit unit, final String fallbackPath) {

        if (configuration.hasPath(path)) {
            return configuration.getDuration(path, unit);
        }

        return configuration.getDuration(fallbackPath, unit);
    }

    /**
     * Gets a value as an Enum.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param enumClassType An enum class
     * @param defaultValue  The default value returned if path does not exist or value is null
     * @return The Enum value at the requested path
     * @since 1.9.0
     */
    public static <T extends Enum<T>> T getEnumOrDefault(final Config configuration, final String path, final Class<T> enumClassType, final T defaultValue) {

        if (configuration.hasPath(path)) {
            return configuration.getEnum(enumClassType, path);
        }

        return defaultValue;
    }

    /**
     * Gets a value as a Enum.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param enumClassType An enum class
     * @param fallbackPath  Path expression to use as fallback
     * @return The Enum value at the requested path
     * @since 1.9.0
     */
    public static <T extends Enum<T>> T getEnumOrFallback(final Config configuration,
                                                          final String path,
                                                          final Class<T> enumClassType,
                                                          final String fallbackPath) {

        if (configuration.hasPath(path)) {
            return configuration.getEnum(enumClassType, path);
        }

        return configuration.getEnum(enumClassType, fallbackPath);
    }

    /**
     * Gets the integer at the given path.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param defaultValue  The default value returned if path does not exist or value is null
     * @return The integer value at the requested path
     * @since 1.9.0
     */
    public static int getIntOrDefault(final Config configuration, final String path, final int defaultValue) {

        if (configuration.hasPath(path)) {
            return configuration.getInt(path);
        }

        return defaultValue;
    }

    /**
     * Gets the integer at the given path.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param fallbackPath  Path expression to use as fallback
     * @return The integer value at the requested path
     * @since 1.9.0
     */
    public static int getIntOrFallback(final Config configuration, final String path, final String fallbackPath) {

        if (configuration.hasPath(path)) {
            return configuration.getInt(path);
        }

        return configuration.getInt(fallbackPath);
    }

    /**
     * Gets the long at the given path.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param defaultValue  The default value returned if path does not exist or value is null
     * @return The long value at the requested path
     * @since 1.9.0
     */
    public static long getLongOrDefault(final Config configuration, final String path, final long defaultValue) {

        if (configuration.hasPath(path)) {
            return configuration.getLong(path);
        }

        return defaultValue;
    }

    /**
     * Gets the long at the given path.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param fallbackPath  Path expression to use as fallback
     * @return The long value at the requested path
     * @since 1.9.0
     */
    public static long getLongOrFallback(final Config configuration, final String path, final String fallbackPath) {

        if (configuration.hasPath(path)) {
            return configuration.getLong(path);
        }

        return configuration.getLong(fallbackPath);
    }

    /**
     * Gets the string at the given path.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param defaultValue  The default value returned if path does not exist or value is null
     * @return The string value at the requested path
     * @since 1.9.0
     */
    public static String getStringOrDefault(final Config configuration, final String path, final String defaultValue) {

        if (configuration.hasPath(path)) {
            return configuration.getString(path);
        }

        return defaultValue;
    }

    /**
     * Gets the string at the given path.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param fallbackPath  Path expression to use as fallback
     * @return The string value at the requested path
     * @since 1.9.0
     */
    public static String getStringOrFallback(final Config configuration, final String path, final String fallbackPath) {

        if (configuration.hasPath(path)) {
            return configuration.getString(path);
        }

        return configuration.getString(fallbackPath);
    }

    /**
     * Gets a value as a TemporalAmount.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param defaultValue  The default value returned if path does not exist or value is null
     * @return The TemporalAmount value at the requested path
     * @since 1.9.0
     */
    public static TemporalAmount getTemporalOrDefault(final Config configuration, final String path, final TemporalAmount defaultValue) {

        if (configuration.hasPath(path)) {
            return configuration.getTemporal(path);
        }

        return defaultValue;
    }

    /**
     * Gets a value as a TemporalAmount.
     *
     * @param configuration The application configuration
     * @param path          Path expression
     * @param fallbackPath  Path expression to use as fallback
     * @return The TemporalAmount value at the requested path
     * @since 1.9.0
     */
    public static TemporalAmount getTemporalOrFallback(final Config configuration, final String path, final String fallbackPath) {

        if (configuration.hasPath(path)) {
            return configuration.getTemporal(path);
        }

        return configuration.getTemporal(fallbackPath);
    }

    /**
     * Checks whether a value is present and non-null for at least one of the given paths.
     *
     * @return {@code true} if value is present and non-null for at least one of the given paths, otherwise {@code false}
     * @since 1.10.0
     */
    public static boolean hasAnyPath(final Config configuration, final String... pathArray) {

        return Arrays.stream(pathArray).anyMatch(configuration::hasPath);
    }
}
