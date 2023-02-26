package dev.voidframework.core.classestoload;

/**
 * Converter information.
 *
 * @param sourceTypeClass    The source type class
 * @param targetTypeClass    The target type class
 * @param converterTypeClass The converter type class
 * @since 1.0.0
 */
public record ConverterInformation(Class<?> sourceTypeClass,
                                   Class<?> targetTypeClass,
                                   Class<?> converterTypeClass) {
}
