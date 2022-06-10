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
     * @param <SOURCE_TYPE>   The source generic type
     * @param <TARGET_TYPE>   The target generic type
     * @return {@code true} if conversion is possible, otherwise, {@code false}
     */
    <SOURCE_TYPE, TARGET_TYPE> boolean canConvert(final Class<SOURCE_TYPE> sourceTypeClass,
                                                  final Class<TARGET_TYPE> targetTypeClass);

    /**
     * Returns whether the given object can be converted to the given target type.
     *
     * @param object          The source object
     * @param targetTypeClass The target type class
     * @param <TARGET_TYPE>   The target generic type
     * @return {@code true} if conversion is possible, otherwise, {@code false}
     */
    <TARGET_TYPE> boolean canConvert(final Object object,
                                     final Class<TARGET_TYPE> targetTypeClass);

    /**
     * Converts an object to the target type.
     *
     * @param object          The object to convert
     * @param targetTypeClass The target type class
     * @param <SOURCE_TYPE>   The source generic type
     * @param <TARGET_TYPE>   The target generic type
     * @return converted object
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <SOURCE_TYPE, TARGET_TYPE> TARGET_TYPE convert(final SOURCE_TYPE object,
                                                   final Class<TARGET_TYPE> targetTypeClass);

    /**
     * Converts an object to the target type.
     *
     * @param object          The object to convert
     * @param sourceTypeClass The source type class
     * @param targetTypeClass The target type class
     * @param <SOURCE_TYPE>   The source generic type
     * @param <TARGET_TYPE>   The target generic type
     * @return converted object
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <SOURCE_TYPE, TARGET_TYPE> TARGET_TYPE convert(final SOURCE_TYPE object,
                                                   final Class<SOURCE_TYPE> sourceTypeClass,
                                                   final Class<TARGET_TYPE> targetTypeClass);

    /**
     * Converts each object from an iterable to the target type.
     *
     * @param objectIterable  The iterable of objects to convert
     * @param targetTypeClass The target type class
     * @param <SOURCE_TYPE>   The source generic type
     * @param <TARGET_TYPE>   The target generic type
     * @return converted collection of objects
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <SOURCE_TYPE, TARGET_TYPE> List<TARGET_TYPE> convert(final Iterable<SOURCE_TYPE> objectIterable,
                                                         final Class<TARGET_TYPE> targetTypeClass);

    /**
     * Converts each object from a set to the target type.
     *
     * @param objectIterable  The iterable of objects to convert
     * @param sourceTypeClass The source type class
     * @param targetTypeClass The target type class
     * @param <SOURCE_TYPE>   The source generic type
     * @param <TARGET_TYPE>   The target generic type
     * @return converted collection of objects
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <SOURCE_TYPE, TARGET_TYPE> List<TARGET_TYPE> convert(final Iterable<SOURCE_TYPE> objectIterable,
                                                         final Class<SOURCE_TYPE> sourceTypeClass,
                                                         final Class<TARGET_TYPE> targetTypeClass);

    /**
     * Converts each object from a list to the target type.
     *
     * @param objectList      The list of objects to convert
     * @param targetTypeClass The target type class
     * @param <SOURCE_TYPE>   The source generic type
     * @param <TARGET_TYPE>   The target generic type
     * @return converted collection of objects
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <SOURCE_TYPE, TARGET_TYPE> List<TARGET_TYPE> convert(final List<SOURCE_TYPE> objectList,
                                                         final Class<TARGET_TYPE> targetTypeClass);

    /**
     * Converts each object from a list to the target type.
     *
     * @param objectList      The list of objects to convert
     * @param sourceTypeClass The source type class
     * @param targetTypeClass The target type class
     * @param <SOURCE_TYPE>   The source generic type
     * @param <TARGET_TYPE>   The target generic type
     * @return converted collection of objects
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <SOURCE_TYPE, TARGET_TYPE> List<TARGET_TYPE> convert(final List<SOURCE_TYPE> objectList,
                                                         final Class<SOURCE_TYPE> sourceTypeClass,
                                                         final Class<TARGET_TYPE> targetTypeClass);

    /**
     * Converts each object from a set to the target type.
     *
     * @param objectList      The set of objects to convert
     * @param targetTypeClass The target type class
     * @param <SOURCE_TYPE>   The source generic type
     * @param <TARGET_TYPE>   The target generic type
     * @return converted collection of objects
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <SOURCE_TYPE, TARGET_TYPE> Set<TARGET_TYPE> convert(final Set<SOURCE_TYPE> objectList,
                                                        final Class<TARGET_TYPE> targetTypeClass);

    /**
     * Converts each object from a set to the target type.
     *
     * @param objectList      The set of objects to convert
     * @param sourceTypeClass The source type class
     * @param targetTypeClass The target type class
     * @param <SOURCE_TYPE>   The source generic type
     * @param <TARGET_TYPE>   The target generic type
     * @return converted collection of objects
     * @throws ConversionException.ConverterDoesNotExist If the needed converter does not exist
     */
    <SOURCE_TYPE, TARGET_TYPE> Set<TARGET_TYPE> convert(final Set<SOURCE_TYPE> objectList,
                                                        final Class<SOURCE_TYPE> sourceTypeClass,
                                                        final Class<TARGET_TYPE> targetTypeClass);
}
