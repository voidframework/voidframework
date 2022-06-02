package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * Convert a {@code String} into an {@code Byte}.
 */
public class StringToCharacterConverter implements TypeConverter<String, Character> {

    @Override
    public Character convert(final String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }

        return source.charAt(0);
    }
}
