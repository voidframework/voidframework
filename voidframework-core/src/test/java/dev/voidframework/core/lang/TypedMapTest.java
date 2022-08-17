package dev.voidframework.core.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public final class TypedMapTest {

    private static final TypedMap.Key<String> STRING_KEY = TypedMap.Key.of("STRING_KEY");
    private static final TypedMap.Key<Integer> INTEGER_KEY = TypedMap.Key.of("INTEGER_KEY");

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
        final TypedMap.Key<String> key = TypedMap.Key.of("STRING_KEY");

        // Assert
        Assertions.assertEquals(STRING_KEY, key);
    }
}
