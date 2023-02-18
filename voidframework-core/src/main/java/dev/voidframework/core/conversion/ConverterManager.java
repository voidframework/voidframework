package dev.voidframework.core.conversion;

/**
 * Handle all registered converters.
 *
 * @since 1.0.0
 */
public interface ConverterManager {

    /**
     * Returns whether the Manager have converter for the given input / output type.
     *
     * @param sourceClassType The source type class
     * @param targetClassType The target type class
     * @param <S>             The source generic type
     * @param <T>             The target generic type
     * @return {@code true} if the manager has a matching converter, otherwise {@code false}
     * @since 1.0.0
     */
    <S, T> boolean hasConvertFor(final Class<S> sourceClassType, final Class<T> targetClassType);

    /**
     * Register converter.
     *
     * @param sourceClassType The source type class
     * @param targetClassType The target type class
     * @param converter       The converter
     * @since 1.0.0
     */
    void registerConverter(final Class<?> sourceClassType, final Class<?> targetClassType, final TypeConverter<?, ?> converter);

    /**
     * Returns whether the Manager have converter for the given input / output type.
     *
     * @param sourceClassType The source type class
     * @param targetClassType The target type class
     * @param <S>             The source generic type
     * @param <T>             The target generic type
     * @return The requested converter
     * @since 1.0.0
     */
    <S, T> TypeConverter<S, T> getConverter(final Class<S> sourceClassType, final Class<T> targetClassType);

    /**
     * Returns the number of registered converters.
     *
     * @return The number of registered converters
     * @since 1.0.0
     */
    int count();
}
