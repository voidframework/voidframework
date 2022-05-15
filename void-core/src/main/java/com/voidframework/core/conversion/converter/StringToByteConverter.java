package com.voidframework.core.conversion.converter;

import com.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Byte}.
 */
public class StringToByteConverter implements TypeConverter<String, Byte> {

    @Override
    public Byte convert(final String source) {
        if (source == null) {
            return null;
        }

        try {
            return Byte.valueOf(source);
        } catch (final NumberFormatException ignore) {
            return null;
        }
    }
}
