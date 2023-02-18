package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Long}.
 *
 * @since 1.0.0
 */
public class StringToLongConverter implements TypeConverter<String, Long> {

    @Override
    public Long convert(final String source) {

        try {
            return Long.valueOf(source);
        } catch (final NumberFormatException ignore) {
            return null;
        }
    }
}
