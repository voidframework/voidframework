package sample.converter;

import dev.voidframework.core.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code String}.
 */
public class StringToStringConverter implements TypeConverter<String, String> {

    @Override
    public String convert(final String source) {
        return source;
    }
}
