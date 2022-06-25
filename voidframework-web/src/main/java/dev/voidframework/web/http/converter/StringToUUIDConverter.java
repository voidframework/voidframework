package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;

import java.util.UUID;

/**
 * Convert a {@code String} into an {@code UUID}.
 */
public class StringToUUIDConverter implements TypeConverter<String, UUID> {

    @Override
    public UUID convert(final String source) {

        try {
            return UUID.fromString(source);
        } catch (final IllegalArgumentException ignore) {
            return null;
        }
    }
}
