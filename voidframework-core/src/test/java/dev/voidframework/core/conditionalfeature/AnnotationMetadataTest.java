package dev.voidframework.core.conditionalfeature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class AnnotationMetadataTest {

    @Test
    void getBoolean() {

        // Arrange
        final Map<String, Object> initialData = Map.of("keyTrue", true, "keyFalse", false);
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final boolean valueTrue = annotationMetadata.getBoolean("keyTrue");
        final boolean valueFalse = annotationMetadata.getBoolean("keyFalse");

        // Assert
        Assertions.assertTrue(valueTrue);
        Assertions.assertFalse(valueFalse);
    }

    @Test
    void getBooleanArray() {

        // Arrange
        final Map<String, Object> initialData = Map.of("key", new boolean[]{true, true, false});
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final boolean[] valueArray = annotationMetadata.getBooleanArray("key");
        final boolean[] valueArrayNull = annotationMetadata.getBooleanArray("unknown");

        // Assert
        Assertions.assertArrayEquals(new boolean[]{true, true, false}, valueArray);
        Assertions.assertNull(valueArrayNull);
    }

    @Test
    void getChar() {

        // Arrange
        final Map<String, Object> initialData = Map.of("key", 'c');
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final char value = annotationMetadata.getChar("key");

        // Assert
        Assertions.assertEquals('c', value);
    }

    @Test
    void getCharArray() {

        // Arrange
        final Map<String, Object> initialData = Map.of("key", new char[]{'c', 'z'});
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final char[] valueArray = annotationMetadata.getCharArray("key");
        final char[] valueArrayNull = annotationMetadata.getCharArray("keyUnknown");

        // Assert
        Assertions.assertArrayEquals(new char[]{'c', 'z'}, valueArray);
        Assertions.assertNull(valueArrayNull);
    }

    @Test
    void getClassType() {

        // Arrange
        final Map<String, Object> initialData = Map.of("key", String.class);
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final Class<?> value = annotationMetadata.getClassType("key");

        // Assert
        Assertions.assertEquals(String.class, value);
    }

    @Test
    void getClassTypeArray() {

        // Arrange
        final Map<String, Object> initialData = Map.of("key", new Class<?>[]{String.class, Number.class});
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final Class<?>[] valueArray = annotationMetadata.getClassTypeArray("key");
        final Class<?>[] valueArrayNull = annotationMetadata.getClassTypeArray("keyUnknown");

        // Assert
        Assertions.assertArrayEquals(new Class<?>[]{String.class, Number.class}, valueArray);
        Assertions.assertNull(valueArrayNull);
    }

    @Test
    void getEnumeration() {

        // Arrange
        final Map<String, Object> initialData = Map.of("key", DummyEnumeration.FIGHTER);
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final DummyEnumeration value = annotationMetadata.getEnumeration("key");

        // Assert
        Assertions.assertEquals(DummyEnumeration.FIGHTER, value);
    }

    @Test
    void getEnumerationArray() {

        // Arrange
        final Map<String, Object> initialData = Map.of("key", new DummyEnumeration[]{DummyEnumeration.FIGHTER, DummyEnumeration.WIZARD});
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final Enum<?>[] valueArray = annotationMetadata.getEnumerationArray("key");
        final Enum<?>[] valueArrayNull = annotationMetadata.getEnumerationArray("keyUnknown");

        // Assert
        Assertions.assertArrayEquals(new DummyEnumeration[]{DummyEnumeration.FIGHTER, DummyEnumeration.WIZARD}, valueArray);
        Assertions.assertNull(valueArrayNull);
    }

    @Test
    void getInteger() {

        // Arrange
        final Map<String, Object> initialData = Map.of("key", 1337);
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final int value = annotationMetadata.getInteger("key");

        // Assert
        Assertions.assertEquals(1337, value);
    }

    @Test
    void getIntegerArray() {

        // Arrange
        final Map<String, Object> initialData = Map.of("key", new int[]{1, 2, 3});
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final int[] valueArray = annotationMetadata.getIntegerArray("key");
        final int[] valueArrayNull = annotationMetadata.getIntegerArray("keyUnknown");

        // Assert
        Assertions.assertArrayEquals(new int[]{1, 2, 3}, valueArray);
        Assertions.assertNull(valueArrayNull);
    }

    @Test
    void getLong() {

        // Arrange
        final Map<String, Object> initialData = Map.of("key", 1337L);
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final long value = annotationMetadata.getLong("key");

        // Assert
        Assertions.assertEquals(1337L, value);
    }

    @Test
    void getLongArray() {

        // Arrange
        final Map<String, Object> initialData = Map.of("key", new long[]{1L, 2L, 3L});
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final long[] valueArray = annotationMetadata.getLongArray("key");
        final long[] valueArrayNull = annotationMetadata.getLongArray("keyUnknown");

        // Assert
        Assertions.assertArrayEquals(new long[]{1L, 2L, 3L}, valueArray);
        Assertions.assertNull(valueArrayNull);
    }

    @Test
    void getString() {

        // Arrange
        final Map<String, Object> initialData = Map.of("key", "Pear");
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final String value = annotationMetadata.getString("key");
        final String valueNull = annotationMetadata.getString("unknown");

        // Assert
        Assertions.assertEquals("Pear", value);
        Assertions.assertNull(valueNull);
    }

    @Test
    void getStringArray() {

        // Arrange
        final Map<String, Object> initialData = Map.of("key", new String[]{"apple", "banana", "pear"});
        final AnnotationMetadata annotationMetadata = new AnnotationMetadata(initialData);

        // Act
        final String[] valueArray = annotationMetadata.getStringArray("key");
        final String[] valueArrayNull = annotationMetadata.getStringArray("keyUnknown");

        // Assert
        Assertions.assertArrayEquals(new String[]{"apple", "banana", "pear"}, valueArray);
        Assertions.assertNull(valueArrayNull);
    }

    /**
     * Dummy enumeration.
     */
    private enum DummyEnumeration {

        FIGHTER,
        WIZARD
    }
}
