package dev.voidframework.core.conversion.impl;

/**
 * Composite key consisting of the source and destination of the conversion.
 *
 * @since 1.0.0
 */
public record ConverterCompositeKey(Class<?> sourceTypeClass,
                                    Class<?> targetTypeClass) {
}
