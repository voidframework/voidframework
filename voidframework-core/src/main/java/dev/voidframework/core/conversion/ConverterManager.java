package dev.voidframework.core.conversion;

/**
 * Handle all registered converters.
 */
public interface ConverterManager {

    /**
     * Return whether the Manager have converter for the given input / output type.
     *
     * @param sourceClassType The source type class
     * @param targetClassType The target type class
     * @param <SOURCE_TYPE>   The source generic type
     * @param <TARGET_TYPE>   The target generic type
     * @return {@code true} if the manager has a matching converter, otherwise {@code false}
     */
    <SOURCE_TYPE, TARGET_TYPE> boolean hasConvertFor(final Class<SOURCE_TYPE> sourceClassType,
                                                     final Class<TARGET_TYPE> targetClassType);

    /**
     * Register converter.
     *
     * @param sourceClassType The source type class
     * @param targetClassType The target type class
     * @param converter       The converter
     */
    void registerConverter(final Class<?> sourceClassType,
                           final Class<?> targetClassType,
                           final TypeConverter<?, ?> converter);

    /**
     * Return whether the Manager have converter for the given input / output type.
     *
     * @param sourceClassType The source type class
     * @param targetClassType The target type class
     * @param <SOURCE_TYPE>   The source generic type
     * @param <TARGET_TYPE>   The target generic type
     * @return The requested converter
     */
    <SOURCE_TYPE, TARGET_TYPE> TypeConverter<SOURCE_TYPE, TARGET_TYPE> getConverter(final Class<SOURCE_TYPE> sourceClassType,
                                                                                    final Class<TARGET_TYPE> targetClassType);

    /**
     * Return the number of registered converters.
     *
     * @return The number of registered converters
     */
    int count();
}
