package dev.voidframework.core.utils;

import dev.voidframework.core.constant.StringConstants;

import java.lang.reflect.Method;
import java.util.StringJoiner;

/**
 * Utility methods for class method.
 *
 * @since 1.12.0
 */
public final class ClassMethodUtils {

    /**
     * Default constructor.
     *
     * @since 1.12.0
     */
    private ClassMethodUtils() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Creates method's short signature.
     *
     * @param method Method for which create the short signature
     * @return Short signature of the method
     * @since 1.12.0
     */
    public static String toShortSignature(final Method method) {

        if (method == null) {
            return null;
        }

        final StringJoiner stringJoiner = new StringJoiner(
            StringConstants.COMMA,
            method.getName() + StringConstants.PARENTHESIS_OPEN,
            StringConstants.PARENTHESIS_CLOSE);

        for (final Class<?> parameterType : method.getParameterTypes()) {
            stringJoiner.add(parameterType.getTypeName());
        }

        return stringJoiner.toString();
    }
}
