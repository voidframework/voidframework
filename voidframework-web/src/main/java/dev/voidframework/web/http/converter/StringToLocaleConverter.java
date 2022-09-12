package dev.voidframework.web.http.converter;

import dev.voidframework.core.conversion.TypeConverter;

import java.util.Locale;

/**
 * Convert a {@code String} into an {@code Locale}.
 */
public class StringToLocaleConverter implements TypeConverter<String, Locale> {

    @Override
    public Locale convert(final String source) {

        try {
            return Locale.forLanguageTag(source);
        } catch (final NullPointerException ignore) {
            return null;
        }
    }
}
