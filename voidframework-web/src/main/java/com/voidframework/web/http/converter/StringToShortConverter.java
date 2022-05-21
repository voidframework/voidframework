package com.voidframework.web.http.converter;

import com.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Short}.
 */
public class StringToShortConverter implements TypeConverter<String, Short> {

    @Override
    public Short convert(final String source) {
        if (source == null) {
            return null;
        }

        try {
            return Short.valueOf(source);
        } catch (final NumberFormatException ignore) {
            return null;
        }
    }
}
