package dev.voidframework.core.conversion;

import dev.voidframework.core.exception.ConversionException;

import java.util.List;
import java.util.Set;

/**
 * Conversion from one type to another.
 */
public interface Conversion {

    /**
     * Returns whether the given source type can be converted to the given target type.
     *
     * @param sourceTypeClass The source type class
     * @param targetTypeClass The target type class
     * @param <S>             The source generic type
     * @param <T>             The target generic type
     * @return {@code true} if conversion is possible, otherwise, {@code false}
     */
    <S, T> boolean canConvert(final Class<S> sourceTypeClass, final Class<T> targetTypeClass);

    /**
     * Returns whether the given object can be converted to the given target type.
     *
     * @param object          The source object
     * @param targetTypeClass The target type class
     * @param <T>             The target generic type
     * @return {@code true} if conversion is possible, otherwise, {@code false}
     */
    <T> boolean canConvert(final Object object, final Class<T> targetTypeClass);

    /**
     * Converts an object to the target type.
     *
     * @param object          The object to convert
     * @param targetTypeClass The target type class
     * @param <S>             The source generic type
     * @param <T>             The target generic type
     * @return converted object
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <S, T> T convert(final S object, final Class<T> targetTypeClass);

    /**
     * Converts an object to the target type.
     *
     * @param object          The object to convert
     * @param sourceTypeClass The source type class
     * @param targetTypeClass The target type class
     * @param <S>             The source generic type
     * @param <T>             The target generic type
     * @return converted object
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <S, T> T convert(final S object, final Class<S> sourceTypeClass, final Class<T> targetTypeClass);

    /**
     * Converts each object from an iterable to the target type.
     *
     * @param objectIterable  The iterable of objects to convert
     * @param targetTypeClass The target type class
     * @param <S>             The source generic type
     * @param <T>             The target generic type
     * @return converted collection of objects
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <S, T> List<T> convert(final Iterable<S> objectIterable, final Class<T> targetTypeClass);

    /**
     * Converts each object from a set to the target type.
     *
     * @param objectIterable  The iterable of objects to convert
     * @param sourceTypeClass The source type class
     * @param targetTypeClass The target type class
     * @param <S>             The source generic type
     * @param <T>             The target generic type
     * @return converted collection of objects
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <S, T> List<T> convert(final Iterable<S> objectIterable, final Class<S> sourceTypeClass, final Class<T> targetTypeClass);

    /**
     * Converts each object from a list to the target type.
     *
     * @param objectList      The list of objects to convert
     * @param targetTypeClass The target type class
     * @param <S>             The source generic type
     * @param <T>             The target generic type
     * @return converted collection of objects
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <S, T> List<T> convert(final List<S> objectList, final Class<T> targetTypeClass);

    /**
     * Converts each object from a list to the target type.
     *
     * @param objectList      The list of objects to convert
     * @param sourceTypeClass The source type class
     * @param targetTypeClass The target type class
     * @param <S>             The source generic type
     * @param <T>             The target generic type
     * @return converted collection of objects
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <S, T> List<T> convert(final List<S> objectList, final Class<S> sourceTypeClass, final Class<T> targetTypeClass);

    /**
     * Converts each object from a set to the target type.
     *
     * @param objectList      The set of objects to convert
     * @param targetTypeClass The target type class
     * @param <S>             The source generic type
     * @param <T>             The target generic type
     * @return converted collection of objects
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <S, T> Set<T> convert(final Set<S> objectList, final Class<T> targetTypeClass);

    /**
     * Converts each object from a set to the target type.
     *
     * @param objectList      The set of objects to convert
     * @param sourceTypeClass The source type class
     * @param targetTypeClass The target type class
     * @param <S>             The source generic type
     * @param <T>             The target generic type
     * @return converted collection of objects
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <S, T> Set<T> convert(final Set<S> objectList, final Class<S> sourceTypeClass, final Class<T> targetTypeClass);
}
