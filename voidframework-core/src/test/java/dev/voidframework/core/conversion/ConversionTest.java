package dev.voidframework.core.conversion;

import dev.voidframework.core.conversion.impl.ConverterCompositeKey;
import dev.voidframework.core.conversion.impl.DefaultConverterManager;
import dev.voidframework.core.helper.Reflection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
public final class ConversionTest {

    @Test
    void registerConverter() {

        // Arrange
        final ConverterManager converterManager = new DefaultConverterManager();
        final TypeConverter<String, Integer> stringToIntegerConverter = new StringToIntegerConverter();

        // Act
        converterManager.registerConverter(String.class, Integer.class, stringToIntegerConverter);

        // Assert
        Assertions.assertEquals(1, converterManager.count());
        Assertions.assertTrue(converterManager.hasConvertFor(String.class, Integer.class));

        final Map<ConverterCompositeKey, TypeConverter<?, ?>> converterMap = Reflection.getFieldValue(
            converterManager,
            "converterMap",
            new Reflection.WrappedClass<>());
        Assertions.assertNotNull(converterMap);
        Assertions.assertEquals(stringToIntegerConverter, converterMap.values().stream().findFirst().orElse(null));
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
