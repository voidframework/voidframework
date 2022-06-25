package dev.voidframework.core.conversion.impl;

/**
 * Composite key consisting of the source and destination of the conversion.
 */
public record ConverterCompositeKey(Class<?> sourceTypeClass,
                                    Class<?> targetTypeClass) {
}
