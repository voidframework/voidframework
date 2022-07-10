package dev.voidframework.core.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Reflection-based utility methods.
 */
public final class Reflection {

    /**
     * Retrieves a specific annotated field.
     *
     * @param classInstance       The instance of the class in which the field is located
     * @param annotationClassType The annotation
     * @return The field, otherwise, null
     */
    public static Field getAnnotatedField(final Object classInstance,
                                          final Class<? extends Annotation> annotationClassType) {

        if (classInstance != null) {
            return getAnnotatedField(classInstance.getClass(), annotationClassType);
        }

        return null;
    }

    /**
     * Retrieves a specific annotated field.
     *
     * @param classType           The class type in which the field is located
     * @param annotationClassType The annotation
     * @return The field, otherwise, null
     */
    public static Field getAnnotatedField(final Class<?> classType,
                                          final Class<? extends Annotation> annotationClassType) {

        try {
            Class<?> currentClassType = classType;
            while (currentClassType != Object.class) {
                for (final Field field : currentClassType.getDeclaredFields()) {
                    if (field.isAnnotationPresent(annotationClassType)) {
                        return field;
                    }
                }

                currentClassType = currentClassType.getSuperclass();
            }
        } catch (final Exception ignore) {
        }

        return null;
    }

    /**
     * Retrieves the value of a specific field.
     *
     * @param classInstance  The instance of the class in which the field is located
     * @param fieldName      The field name
     * @param valueTypeClass The value class type
     * @param <VALUE_TYPE>   The value type
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
     * Sets the value of a specific field.
     *
     * @param classInstance The instance of the class in which the field is located
     * @param fieldName     The field name
     * @param value         The value
     */
    public static void setFieldValue(final Object classInstance,
                                     final String fieldName,
                                     final Object value) {

        try {
            final Field field = classInstance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(classInstance, value);
            field.setAccessible(false);
        } catch (final Exception ignore) {
        }
    }

    /**
     * Resolves a methode from it name.
     *
     * @param methodName The method name
     * @param classType  The class where are located this method
     * @return The method
     */
    public static Method resolveMethod(final String methodName, final Class<?> classType) {

        for (final Method method : classType.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        return null;
    }

    /**
     * Calls a method.
     *
     * @param classInstance     The instance of the class in which the method is located
     * @param methodeName       The method name
     * @param returnTypeClass   The returned value type
     * @param argumentTypeArray The method argument types
     * @param argumentArray     The method arguments
     * @param <RETURN_TYPE>     Type of the return value
     * @return The method call result
     */
    public static <RETURN_TYPE> RETURN_TYPE callMethod(final Object classInstance,
                                                       final String methodeName,
                                                       final Class<RETURN_TYPE> returnTypeClass,
                                                       final Class<?>[] argumentTypeArray,
                                                       final Object... argumentArray) {

        try {
            final Method method = classInstance.getClass().getDeclaredMethod(methodeName, argumentTypeArray);
            method.setAccessible(true);

            final RETURN_TYPE ret = returnTypeClass.cast(method.invoke(classInstance, argumentArray));
            method.setAccessible(false);

            return ret;
        } catch (final Exception ignore) {
            return null;
        }
    }

    /**
     * Calls a method.
     *
     * @param classInstance     The instance of the class in which the method is located
     * @param methodeName       The method name
     * @param argumentTypeArray The method argument types
     * @param argumentArray     The method arguments
     */
    public static void callMethod(final Object classInstance,
                                  final String methodeName,
                                  final Class<?>[] argumentTypeArray,
                                  final Object... argumentArray) {

        try {
            final Method method = classInstance.getClass().getDeclaredMethod(methodeName, argumentTypeArray);
            method.setAccessible(true);
            method.invoke(classInstance, argumentArray);
            method.setAccessible(false);
        } catch (final Exception ignore) {
        }
    }

    /**
     * Allows to wrap a complex class type (ie: Map{String, Integer})
     *
     * @param <CLASS_TYPE> The wrapped class type
     */
    public static final class WrappedClass<CLASS_TYPE> {

        private final Class<CLASS_TYPE> classType;

        /**
         * Build a new instance.
         */
        @SuppressWarnings("unchecked")
        public WrappedClass() {

            this.classType = (Class<CLASS_TYPE>) this.getClass().getSuperclass();
        }

        /**
         * Returns the wrapped class type.
         *
         * @return The wrapped class type
         */
        public Class<CLASS_TYPE> getWrappedClass() {

            return this.classType;
        }
    }
}
