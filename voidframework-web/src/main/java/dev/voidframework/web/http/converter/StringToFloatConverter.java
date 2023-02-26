package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Float}.
 *
 * @since 1.0.0
 */
public class StringToFloatConverter implements TypeConverter<String, Float> {

    @Override
    public Float convert(final String source) {

        try {
            return Float.valueOf(source);
        } catch (final NumberFormatException ignore) {
            return null;
        }
    }
}
