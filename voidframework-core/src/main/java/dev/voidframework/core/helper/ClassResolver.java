package dev.voidframework.core.helper;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper to resolve class.
 */
public final class ClassResolver {

    /**
     * Resolves {@code Class<?>} for a class name.
     *
     * @param className Name of the class
     * @return Resolved {@code Class<?>}
     */
    public static Class<?> forName(final String className) {
        if (StringUtils.isBlank(className)) {
            return null;
        }

        try {
            return Class.forName(className);
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
}
