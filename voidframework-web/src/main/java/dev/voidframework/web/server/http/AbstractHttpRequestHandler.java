package dev.voidframework.web.server.http;

import dev.voidframework.core.conversion.Conversion;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * Defines common methods for all Http request handler implementation (Http, WebSocket, ...).
 */
abstract class AbstractHttpRequestHandler {

    protected static final String EMPTY_FALLBACK_VALUE = "\00\00";

    private static final Map<Class<?>, PrimitiveAlternative> PRIMITIVE_ALTERNATIVE_MAP = Map.ofEntries(
        Map.entry(boolean.class, new PrimitiveAlternative(Boolean.class, false)),
        Map.entry(byte.class, new PrimitiveAlternative(Byte.class, 0)),
        Map.entry(char.class, new PrimitiveAlternative(Character.class, 0)),
        Map.entry(double.class, new PrimitiveAlternative(Double.class, 0d)),
        Map.entry(float.class, new PrimitiveAlternative(Float.class, 0f)),
        Map.entry(int.class, new PrimitiveAlternative(Integer.class, 0)),
        Map.entry(long.class, new PrimitiveAlternative(Long.class, 0)),
        Map.entry(short.class, new PrimitiveAlternative(Short.class, 0)));

    private final Conversion conversion;

    /**
     * Build a new instance.
     *
     * @param conversion The conversion instance
     */
    AbstractHttpRequestHandler(final Conversion conversion) {

        this.conversion = conversion;
    }

    /**
     * Try to convert value from a String into the needed parameter type.
     *
     * @param value              The string containing the value to convert
     * @param parameterTypeClass The needed output parameter type class
     * @return The converter value, otherwise, null
     */
    protected Object convertValueToParameterType(final String value, final Class<?> parameterTypeClass) {

        if (parameterTypeClass == String.class) {
            return value;
        }

        Class<?> clazzToUse = parameterTypeClass;
        Object defaultValue = null;

        final PrimitiveAlternative primitiveAlternative = PRIMITIVE_ALTERNATIVE_MAP.get(parameterTypeClass);
        if (primitiveAlternative != null) {
            clazzToUse = primitiveAlternative.replacementClass;
            defaultValue = primitiveAlternative.defaultValue;
        }

        final Object converterValue = conversion.convert(value, clazzToUse);
        return converterValue != null ? converterValue : defaultValue;
    }

    /**
     * Try to convert multiple values from a String into an array of needed parameter type.
     *
     * @param valueList          The list of string containing the value to convert
     * @param parameterTypeClass The needed output parameter type class
     * @return The converter values as array, otherwise, null
     */
    protected Object convertValuesToParameterTypeArray(final List<String> valueList, final Class<?> parameterTypeClass) {

        if (valueList == null) {
            return null;
        }

        final Object target = Array.newInstance(parameterTypeClass, valueList.size());

        int idx = 0;
        for (final String value : valueList) {
            Array.set(target, idx, this.convertValueToParameterType(value, parameterTypeClass));
            idx += 1;
        }

        return target;
    }

    /**
     * Defines an alternative for primitive value conversion.
     *
     * @param replacementClass The remplacement class
     * @param defaultValue     The default value if converter return {@code null}
     */
    private record PrimitiveAlternative(Class<?> replacementClass, Object defaultValue) {
    }
}
