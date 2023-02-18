package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;
import dev.voidframework.core.lang.CUID;

/**
 * Convert a {@code String} into an {@code CUID}.
 *
 * @since 1.3.0
 */
public class StringToCUIDConverter implements TypeConverter<String, CUID> {

    @Override
    public CUID convert(final String source) {

        try {
            return CUID.fromString(source);
        } catch (final IllegalArgumentException ignore) {
            return null;
        }
    }
}
