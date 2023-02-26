package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Integer}.
 *
 * @since 1.0.0
 */
public class StringToIntegerConverter implements TypeConverter<String, Integer> {

    @Override
    public Integer convert(final String source) {

        try {
            return Integer.valueOf(source);
        } catch (final NumberFormatException ignore) {
            return null;
        }
    }
}
