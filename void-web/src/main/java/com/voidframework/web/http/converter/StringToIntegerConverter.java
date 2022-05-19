package com.voidframework.web.http.converter;

import com.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Integer}.
 */
public class StringToIntegerConverter implements TypeConverter<String, Integer> {

    @Override
    public Integer convert(final String source) {
        if (source == null) {
            return null;
        }

        try {
            return Integer.valueOf(source);
        } catch (final NumberFormatException ignore) {
            return null;
        }
    }
}
