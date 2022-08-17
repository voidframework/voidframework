package dev.voidframework.core.conversion;

import dev.voidframework.core.conversion.impl.ConverterCompositeKey;
import dev.voidframework.core.conversion.impl.DefaultConverterManager;
import dev.voidframework.core.exception.ConversionException;
import dev.voidframework.core.helper.Reflection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class DefaultConverterManagerTest {

    @Test
    void hasConvertForConverterIsPresent() {

        // Arrange
        final ConverterManager converterManager = new DefaultConverterManager();
        converterManager.registerConverter(Integer.class, String.class, new IntegerToStringConverter());

        // Act
        final boolean hasConverter = converterManager.hasConvertFor(Integer.class, String.class);

        // Assert
        Assertions.assertTrue(hasConverter);
    }

    @Test
    void hasConvertForConverterIsNotPresent() {

        // Arrange
        final ConverterManager converterManager = new DefaultConverterManager();

        // Act
        final boolean hasConverter = converterManager.hasConvertFor(Boolean.class, BigDecimal.class);

        // Assert
        Assertions.assertFalse(hasConverter);
    }

    @Test
    void registerConverter() {

        // Arrange
        final ConverterManager converterManager = new DefaultConverterManager();

        // Act
        converterManager.registerConverter(Integer.class, String.class, new IntegerToStringConverter());

        // Assert
        final ConverterCompositeKey key = new ConverterCompositeKey(Integer.class, String.class);
        final Map<ConverterCompositeKey, TypeConverter<?, ?>> converterMap = Reflection.getFieldValue(
            converterManager,
            "converterMap",
            new Reflection.WrappedClass<>());

        Assertions.assertNotNull(converterMap);
        Assertions.assertEquals(1, converterMap.size());
        Assertions.assertTrue(converterMap.containsKey(key));
        Assertions.assertTrue(converterMap.get(key) instanceof IntegerToStringConverter);
    }

    @Test
    void registerConverterAlreadyRegistered() {

        // Arrange
        final ConverterManager converterManager = new DefaultConverterManager();

        // Act
        converterManager.registerConverter(Integer.class, String.class, new IntegerToStringConverter());
        final ConversionException.ConverterAlreadyRegistered exception = Assertions.assertThrows(
            ConversionException.ConverterAlreadyRegistered.class,
            () -> converterManager.registerConverter(Integer.class, String.class, new IntegerToStringConverter()));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(
            "Converter<source=java.lang.Integer, target=java.lang.String> already registered",
            exception.getMessage());
    }

    /**
     * Converter from Integer to String.
     */
    public static class IntegerToStringConverter implements TypeConverter<Integer, String> {

        @Override
        public String convert(final Integer source) {

            return source.toString();
        }
    }
}
