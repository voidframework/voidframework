package dev.voidframework.core.conversion;

import dev.voidframework.core.conversion.impl.DefaultConverterManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public final class ConversionTest {

    @Test
    public void registerConverter() {

        final ConverterManager converterManager = new DefaultConverterManager();
        converterManager.registerConverter(String.class, Integer.class, new StringToIntegerConverter());

        Assertions.assertEquals(1, converterManager.count());
    }

    /**
     * Convert a {@code String} into {@code Integer}.
     */
    public static class StringToIntegerConverter implements TypeConverter<String, Integer> {

        @Override
        public Integer convert(final String source) {

            try {
                return Integer.valueOf(source);
            } catch (final NumberFormatException ignore) {
                return null;
            }
        }
    }
}
