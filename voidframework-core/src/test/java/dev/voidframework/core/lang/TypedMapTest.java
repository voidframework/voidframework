package dev.voidframework.core.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class TypedMapTest {

    private static final TypedMap.Key<String> STRING_KEY = TypedMap.Key.of("STRING_KEY", String.class);
    private static final TypedMap.Key<Integer> INTEGER_KEY = TypedMap.Key.of("INTEGER_KEY", Integer.class);

    @Test
    void putGetIntegerKey() {

        // Arrange
        final TypedMap typedMap = new TypedMap();
        typedMap.put(INTEGER_KEY, 42);

        // Act
        final Integer value = typedMap.get(INTEGER_KEY);

        // Assert
        Assertions.assertEquals(Integer.valueOf(42), value);
    }

    @Test
    void putGetStringKey() {

        // Arrange
        final TypedMap typedMap = new TypedMap();
        typedMap.put(STRING_KEY, "Hello World!");

        // Act
        final String value = typedMap.get(STRING_KEY);

        // Assert
        Assertions.assertEquals("Hello World!", value);
    }

    @Test
    void remove() {

        // Arrange
        final TypedMap typedMap = new TypedMap();
        typedMap.put(STRING_KEY, "Hello World!");

        // Act
        typedMap.remove(STRING_KEY);

        // Assert
        Assertions.assertNull(typedMap.get(STRING_KEY));
    }

    @Test
    void showString() {

        // Arrange
        final TypedMap typedMap = new TypedMap();

        // Act
        typedMap.put(STRING_KEY, "Hello World!");

        // Assert
        Assertions.assertEquals("{STRING_KEY=Hello World!}", typedMap.toString());
    }

    @Test
    void of1() {

        // Arrange
        final TypedMap.Key<String> key1 = TypedMap.Key.of("KEY_1", String.class);

        // Act
        final TypedMap typedMap = TypedMap.of(key1, "Hello World");

        // Assert
        Assertions.assertNotNull(typedMap);
        Assertions.assertEquals("Hello World", typedMap.get(key1));
    }

    @Test
    void of2() {

        // Arrange
        final TypedMap.Key<String> key1 = TypedMap.Key.of("KEY_1", String.class);
        final TypedMap.Key<LocalDateTime> key2 = TypedMap.Key.of("KEY_2", LocalDateTime.class);

        // Act
        final TypedMap typedMap = TypedMap.of(
            key1, "Hello World",
            key2, LocalDateTime.of(2022, 1, 3, 23, 32, 10));

        // Assert
        Assertions.assertNotNull(typedMap);
        Assertions.assertEquals("Hello World", typedMap.get(key1));
        Assertions.assertEquals(LocalDateTime.of(2022, 1, 3, 23, 32, 10), typedMap.get(key2));
    }

    @Test
    void of3() {

        // Arrange
        final TypedMap.Key<String> key1 = TypedMap.Key.of("KEY_1", String.class);
        final TypedMap.Key<LocalDateTime> key2 = TypedMap.Key.of("KEY_2", LocalDateTime.class);
        final TypedMap.Key<Integer> key3 = TypedMap.Key.of("KEY_3", Integer.class);

        // Act
        final TypedMap typedMap = TypedMap.of(
            key1, "Hello World",
            key2, LocalDateTime.of(2022, 1, 3, 23, 32, 10),
            key3, 1337);

        // Assert
        Assertions.assertNotNull(typedMap);
        Assertions.assertEquals("Hello World", typedMap.get(key1));
        Assertions.assertEquals(LocalDateTime.of(2022, 1, 3, 23, 32, 10), typedMap.get(key2));
        Assertions.assertEquals(1337, typedMap.get(key3));
    }

    @Test
    void of4() {

        // Arrange
        final TypedMap.Key<String> key1 = TypedMap.Key.of("KEY_1", String.class);
        final TypedMap.Key<LocalDateTime> key2 = TypedMap.Key.of("KEY_2", LocalDateTime.class);
        final TypedMap.Key<Integer> key3 = TypedMap.Key.of("KEY_3", Integer.class);
        final TypedMap.Key<Boolean> key4 = TypedMap.Key.of("KEY_4", Boolean.class);

        // Act
        final TypedMap typedMap = TypedMap.of(
            key1, "Hello World",
            key2, LocalDateTime.of(2022, 1, 3, 23, 32, 10),
            key3, 1337,
            key4, true);

        // Assert
        Assertions.assertNotNull(typedMap);
        Assertions.assertEquals("Hello World", typedMap.get(key1));
        Assertions.assertEquals(LocalDateTime.of(2022, 1, 3, 23, 32, 10), typedMap.get(key2));
        Assertions.assertEquals(1337, typedMap.get(key3));
        Assertions.assertTrue(typedMap.get(key4));
    }

    @Test
    void equalsTypeMap() {

        // Arrange
        final TypedMap typedMap1 = new TypedMap();
        final TypedMap typedMap2 = new TypedMap();

        // Act
        typedMap1.put(STRING_KEY, "Hello World!");
        typedMap2.put(STRING_KEY, "Hello World!");

        // Assert
        Assertions.assertEquals(typedMap1, typedMap2);
        Assertions.assertEquals(typedMap1.hashCode(), typedMap2.hashCode());
    }

    @Test
    void equalsKey() {

        // Act
        final TypedMap.Key<String> key = TypedMap.Key.of("STRING_KEY", String.class);

        // Assert
        Assertions.assertEquals(STRING_KEY, key);
    }

    @Test
    void equalsKeyExplicitType() {

        // Act
        final TypedMap.Key<String> key = TypedMap.Key.of("STRING_KEY", String.class);

        // Assert
        Assertions.assertEquals(STRING_KEY, key);
    }
}
