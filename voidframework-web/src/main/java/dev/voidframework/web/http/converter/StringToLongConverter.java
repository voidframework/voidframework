package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Long}.
 */
public class StringToLongConverter implements TypeConverter<String, Long> {

    @Override
    public Long convert(final String source) {
        if (source == null) {
            return null;
        }

        try {
            return Long.valueOf(source);
        } catch (final NumberFormatException ignore) {
            return null;
        }
    }
}
