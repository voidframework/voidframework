package dev.voidframework.core.helper;

import java.lang.reflect.Field;

/**
 * Reflection-based utility methods.
 */
public final class Reflection {

    /**
     * Retrieves the value of a specific field.
     *
     * @param classInstance  The instance of the class in which the field is located
     * @param fieldName      The field name
     * @param valueTypeClass The value class type
     * @return The field value, otherwise, null
     */
    public static <VALUE_TYPE> VALUE_TYPE getFieldValue(final Object classInstance,
                                                        final String fieldName,
                                                        final Class<VALUE_TYPE> valueTypeClass) {
        try {
            final Field field = classInstance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            return valueTypeClass.cast(field.get(classInstance));
        } catch (final Exception ignore) {
            return null;
        }
    }

    /**
     * Retrieves the value of a specific field.
     *
     * @param classInstance The instance of the class in which the field is located
     * @param fieldName     The field name
     * @param wrappedClass  The value class type (wrapped)
     * @param <VALUE_TYPE>  The value type
     * @return The field value, otherwise, null
     */
    public static <VALUE_TYPE> VALUE_TYPE getFieldValue(final Object classInstance,
                                                        final String fieldName,
                                                        final WrappedClass<VALUE_TYPE> wrappedClass) {
        try {
            final Field field = classInstance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            return wrappedClass.getWrappedClass().cast(field.get(classInstance));
        } catch (final Exception ignore) {
            return null;
        }
    }

    /**
     * Allows to wrap a complex class type (ie: Map<String, Integer>)
     *
     * @param <CLASS_TYPE> The wrapped class type
     */
    public static final class WrappedClass<CLASS_TYPE> {

        final Class<CLASS_TYPE> classType;

        @SuppressWarnings("unchecked")
        public WrappedClass() {
            this.classType = (Class<CLASS_TYPE>) this.getClass().getSuperclass();
        }

        public Class<CLASS_TYPE> getWrappedClass() {
            return this.classType;
        }
    }
}
