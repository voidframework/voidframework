package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Byte}.
 */
public class StringToCharacterConverter implements TypeConverter<String, Character> {

    @Override
    public Character convert(final String source) {
        return source.charAt(0);
    }
}
