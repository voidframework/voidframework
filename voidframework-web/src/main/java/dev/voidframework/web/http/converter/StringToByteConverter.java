package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Byte}.
 *
 * @since 1.0.0
 */
public class StringToByteConverter implements TypeConverter<String, Byte> {

    @Override
    public Byte convert(final String source) {

        try {
            return Byte.valueOf(source);
        } catch (final NumberFormatException ignore) {
            return null;
        }
    }
}
