package dev.voidframework.core.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility methods to resolve classes.
 *
 * @since 1.0.0
 */
public final class ClassResolverUtils {

    /**
     * Default constructor.
     *
     * @since 1.0.0
     */
    private ClassResolverUtils() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Resolves {@code Class<?>} for a class name.
     *
     * @param className Name of the class
     * @param <T>       The type of the resolved class
     * @return Resolved {@code Class<?>}
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> forName(final String className) {

        if (StringUtils.isBlank(className)) {
            return null;
        }

        try {
            return (Class<? extends T>) Class.forName(className);
        } catch (final ClassNotFoundException ignore) {
            return null;
        }
    }

    /**
     * Resolves {@code Class<?>} for a class name.
     *
     * @param className   Name of the class
     * @param classLoader Loader to use
     * @return Resolved {@code Class<?>}
     * @since 1.0.0
     */
    public static Class<?> forName(final String className, final ClassLoader classLoader) {

        if (StringUtils.isBlank(className)) {
            return null;
        }

        try {
            return Class.forName(className, false, classLoader);
        } catch (final ClassNotFoundException ignore) {
            return null;
        }
    }

    /**
     * Checks if a class is available.
     *
     * @param className Name of the class
     * @return {@code true} if given class is available, otherwise {@code false}
     * @since 1.11.0
     */
    public static boolean isClassAvailable(final String className) {

        if (StringUtils.isBlank(className)) {
            return false;
        }

        try {
            Class.forName(className);
            return true;
        } catch (final ClassNotFoundException ignore) {
            return false;
        }
    }

    /**
     * Checks if a class is available.
     *
     * @param className   Name of the class
     * @param classLoader Loader to use
     * @return {@code true} if given class is available, otherwise {@code false}
     * @since 1.11.0
     */
    public static boolean isClassAvailable(final String className, final ClassLoader classLoader) {

        if (StringUtils.isBlank(className)) {
            return false;
        }

        try {
            Class.forName(className, false, classLoader);
            return true;
        } catch (final ClassNotFoundException ignore) {
            return false;
        }
    }
}
