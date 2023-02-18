package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Byte}.
 *
 * @since 1.0.0
 */
public class StringToCharacterConverter implements TypeConverter<String, Character> {

    @Override
    public Character convert(final String source) {

        return source.charAt(0);
    }
}
