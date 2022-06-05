package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Convert a {@code String} into an {@code Boolean}.
 */
public class StringToBooleanConverter implements TypeConverter<String, Boolean> {

    private static final List<String> VALUE_TRUE_LIST = Arrays.asList("1", "true", "y", "yes");

    @Override
    public Boolean convert(final String source) {
        return VALUE_TRUE_LIST.contains(source.toLowerCase(Locale.ENGLISH));
    }
}
