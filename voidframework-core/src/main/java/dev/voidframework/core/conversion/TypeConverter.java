package dev.voidframework.core.conversion;

/**
 * All type converters must implement this interface. A type converter must be
 * stateless, simple and thread safe. Even if direct injection is available,
 * it is not recommended to perform too complex, blocking or slow operation.
 *
 * @param <S> The source generic type
 * @param <T> The target generic type
 */
public interface TypeConverter<S, T> {

    /**
     * Converts the given source object to the target type.
     *
     * @param source The source object to convert
     * @return The converted object
     */
    T convert(final S source);
}
