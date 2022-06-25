package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Double}.
 */
public class StringToDoubleConverter implements TypeConverter<String, Double> {

    @Override
    public Double convert(final String source) {

        try {
            return Double.valueOf(source);
        } catch (final NumberFormatException ignore) {
            return null;
        }
    }
}
