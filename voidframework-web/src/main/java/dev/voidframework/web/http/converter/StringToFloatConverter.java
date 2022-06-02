package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Float}.
 */
public class StringToFloatConverter implements TypeConverter<String, Float> {

    @Override
    public Float convert(final String source) {
        if (source == null) {
            return null;
        }

        try {
            return Float.valueOf(source);
        } catch (final NumberFormatException ignore) {
            return null;
        }
    }
}
