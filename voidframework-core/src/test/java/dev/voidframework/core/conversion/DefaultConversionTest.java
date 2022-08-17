package dev.voidframework.core.conversion;

import dev.voidframework.core.conversion.impl.DefaultConversion;
import dev.voidframework.core.conversion.impl.DefaultConverterManager;
import dev.voidframework.core.exception.ConversionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class DefaultConversionTest {

    private final Conversion conversion;

    public DefaultConversionTest() {

        final ConverterManager converterManager = new DefaultConverterManager();
        converterManager.registerConverter(String.class, Integer.class, new StringToIntegerConverter());
        converterManager.registerConverter(Integer.class, String.class, new IntegerToStringConverter());

        this.conversion = new DefaultConversion(converterManager);
    }

    @Test
    void canConvertByClassStringToInteger() {

        // Act
        final boolean canConvert = this.conversion.canConvert(String.class, Integer.class);

        // Assert
        Assertions.assertTrue(canConvert);
    }

    @Test
    void canConvertByClassIntegerToString() {

        // Act
        final boolean canConvert = this.conversion.canConvert(Integer.class, String.class);

        // Assert
        Assertions.assertTrue(canConvert);
    }

    @Test
    void canConvertByClassIntegerToBoolean() {

        // Act
        final boolean canConvert = this.conversion.canConvert(Integer.class, Boolean.class);

        // Assert
        Assertions.assertFalse(canConvert);
    }

    @Test
    void canConvertByInstanceStringToIntegerWithNullValue() {

        // Act
        final boolean canConvert = this.conversion.canConvert((String) null, Integer.class);

        // Assert
        Assertions.assertTrue(canConvert);
    }

    @Test
    void canConvertByInstanceStringToInteger() {

        // Arrange
        final String numberAsString = "10";

        // Act
        final boolean canConvert = this.conversion.canConvert(numberAsString, Integer.class);

        // Assert
        Assertions.assertTrue(canConvert);
    }

    @Test
    void canConvertByInstanceIntegerToString() {

        // Arrange
        final Integer number = 10;

        // Act
        final boolean canConvert = this.conversion.canConvert(number, String.class);

        // Assert
        Assertions.assertTrue(canConvert);
    }

    @Test
    void canConvertByInstanceIntegerToBoolean() {

        // Arrange
        final Integer number = 10;

        // Act
        final boolean canConvert = this.conversion.canConvert(number, Boolean.class);

        // Assert
        Assertions.assertFalse(canConvert);
    }

    @Test
    void convertExplicitConverterNotFound() {

        // Act
        final ConversionException.ConverterDoesNotExist exception = Assertions.assertThrows(
            ConversionException.ConverterDoesNotExist.class,
            () -> this.conversion.convert(Boolean.TRUE, Boolean.class, Integer.class));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(
            "Converter<source=java.lang.Boolean, target=java.lang.Integer> does not exist",
            exception.getMessage());
    }

    @Test
    void convertExplicitNull() {

        // Act
        final Integer convertedValue = this.conversion.convert((String) null, String.class, Integer.class);

        // Assert
        Assertions.assertNull(convertedValue);
    }

    @Test
    void convertExplicit() {

        // Arrange
        final String numberAsString = "10";

        // Act
        final Integer convertedValue = this.conversion.convert(numberAsString, String.class, Integer.class);

        // Assert
        Assertions.assertEquals(Integer.valueOf(10), convertedValue);
    }

    @Test
    void convertImplicitNull() {

        // Act
        final Integer convertedValue = this.conversion.convert((String) null, Integer.class);

        // Assert
        Assertions.assertNull(convertedValue);
    }

    @Test
    void convertImplicit() {

        // Arrange
        final String numberAsString = "10";

        // Act
        final Integer convertedValue = this.conversion.convert(numberAsString, Integer.class);

        // Assert
        Assertions.assertEquals(Integer.valueOf(10), convertedValue);
    }

    @Test
    void convertIterableExplicitNull() {

        // Act
        final List<Integer> convertedValue = this.conversion.convert((Deque<String>) null, String.class, Integer.class);

        // Assert
        Assertions.assertNull(convertedValue);
    }

    @Test
    void convertIterableExplicit() {

        // Arrange
        final Deque<String> numberAsStringDeque = new ArrayDeque<>(List.of("1", "2", "3"));

        // Act
        final List<Integer> convertedValue = this.conversion.convert(numberAsStringDeque, String.class, Integer.class);

        // Assert
        Assertions.assertEquals(List.of(1, 2, 3), convertedValue);
    }

    @Test
    void convertIterableImplicitNull() {

        // Act
        final List<Integer> convertedValue = this.conversion.convert((Deque<String>) null, Integer.class);

        // Assert
        Assertions.assertNull(convertedValue);
    }

    @Test
    void convertIterableImplicit() {

        // Arrange
        final Deque<String> numberAsStringDeque = new ArrayDeque<>(List.of("1", "2", "3"));

        // Act
        final List<Integer> convertedValue = this.conversion.convert(numberAsStringDeque, Integer.class);

        // Assert
        Assertions.assertEquals(List.of(1, 2, 3), convertedValue);
    }

    @Test
    void convertListExplicitNull() {

        // Act
        final List<Integer> convertedValue = this.conversion.convert((List<String>) null, String.class, Integer.class);

        // Assert
        Assertions.assertNull(convertedValue);
    }

    @Test
    void convertListExplicitEmpty() {

        // Act
        final List<Integer> convertedValue = this.conversion.convert(List.of(), String.class, Integer.class);

        // Assert
        Assertions.assertNotNull(convertedValue);
        Assertions.assertTrue(convertedValue.isEmpty());
    }

    @Test
    void convertListExplicit() {

        // Arrange
        final List<String> numberAsStringList = Arrays.asList("1", "2", "3", null);

        // Act
        final List<Integer> convertedValue = this.conversion.convert(numberAsStringList, String.class, Integer.class);

        // Assert
        Assertions.assertEquals(Arrays.asList(1, 2, 3, null), convertedValue);
    }

    @Test
    void convertListImplicitNull() {

        // Act
        final List<Integer> convertedValue = this.conversion.convert((List<String>) null, Integer.class);

        // Assert
        Assertions.assertNull(convertedValue);
    }

    @Test
    void convertListImplicitEmpty() {

        // Act
        final List<Integer> convertedValue = this.conversion.convert(List.of(), Integer.class);

        // Assert
        Assertions.assertNotNull(convertedValue);
        Assertions.assertTrue(convertedValue.isEmpty());
    }

    @Test
    void convertListImplicit() {

        // Arrange
        final List<String> numberAsStringList = Arrays.asList("1", "2", null, "3");

        // Act
        final List<Integer> convertedValue = this.conversion.convert(numberAsStringList, Integer.class);

        // Assert
        Assertions.assertEquals(Arrays.asList(1, 2, null, 3), convertedValue);
    }

    @Test
    void convertSetExplicitNull() {

        // Act
        final Set<Integer> convertedValue = this.conversion.convert((Set<String>) null, String.class, Integer.class);

        // Assert
        Assertions.assertNull(convertedValue);
    }

    @Test
    void convertSetExplicitEmpty() {

        // Act
        final Set<Integer> convertedValue = this.conversion.convert(Set.of(), String.class, Integer.class);

        // Assert
        Assertions.assertNotNull(convertedValue);
        Assertions.assertTrue(convertedValue.isEmpty());
    }

    @Test
    void convertSetExplicit() {

        // Arrange
        final Set<String> numberAsStringList = Set.of("1", "2", "3");

        // Act
        final Set<Integer> convertedValue = this.conversion.convert(numberAsStringList, String.class, Integer.class);

        // Assert
        Assertions.assertEquals(Set.of(1, 2, 3), convertedValue);
    }

    @Test
    void convertSetImplicitNull() {

        // Act
        final Set<Integer> convertedValue = this.conversion.convert((Set<String>) null, Integer.class);

        // Assert
        Assertions.assertNull(convertedValue);
    }

    @Test
    void convertSetImplicitEmpty() {

        // Act
        final Set<Integer> convertedValue = this.conversion.convert(Set.of(), Integer.class);

        // Assert
        Assertions.assertNotNull(convertedValue);
        Assertions.assertTrue(convertedValue.isEmpty());
    }

    @Test
    void convertSetImplicit() {

        // Arrange
        final Set<String> numberAsStringList = Set.of("1", "2", "3");

        // Act
        final Set<Integer> convertedValue = this.conversion.convert(numberAsStringList, Integer.class);

        // Assert
        Assertions.assertEquals(Set.of(1, 2, 3), convertedValue);
    }

    @Test
    void convertExplicitCollectionConverterNotFound() {

        // Arrange
        final List<Boolean> booleanList = List.of(true, false, true);

        // Act
        final ConversionException.ConverterDoesNotExist exception = Assertions.assertThrows(
            ConversionException.ConverterDoesNotExist.class,
            () -> this.conversion.convert(booleanList, Boolean.class, Integer.class));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(
            "Converter<source=java.lang.Boolean, target=java.lang.Integer> does not exist",
            exception.getMessage());
    }

    @Test
    void convertImplicitCollectionConverterNotFound() {

        // Arrange
        final List<Boolean> booleanList = List.of(true, false, true);

        // Act
        final ConversionException.ConverterDoesNotExist exception = Assertions.assertThrows(
            ConversionException.ConverterDoesNotExist.class,
            () -> this.conversion.convert(booleanList, Integer.class));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(
            "Converter<source=java.lang.Boolean, target=java.lang.Integer> does not exist",
            exception.getMessage());
    }

    /**
     * Converter from String to Integer.
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
