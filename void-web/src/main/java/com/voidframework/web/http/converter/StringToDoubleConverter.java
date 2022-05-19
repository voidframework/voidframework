package com.voidframework.web.http.converter;

import com.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Double}.
 */
public class StringToDoubleConverter implements TypeConverter<String, Double> {

    @Override
    public Double convert(final String source) {
        if (source == null) {
            return null;
        }

        try {
            return Double.valueOf(source);
        } catch (final NumberFormatException ignore) {
            return null;
        }
    }
}
