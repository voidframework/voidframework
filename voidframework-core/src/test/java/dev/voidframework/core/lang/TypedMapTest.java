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
    public void putGet() {
        final TypedMap typedMap = new TypedMap();
        typedMap.put(STRING_KEY, "Hello World!");
        typedMap.put(INTEGER_KEY, 42);

        Assertions.assertEquals("Hello World!", typedMap.get(STRING_KEY));
        Assertions.assertEquals(Integer.valueOf(42), typedMap.get(INTEGER_KEY));
    }

    @Test
    public void remove() {
        final TypedMap typedMap = new TypedMap();

        Assertions.assertNull(typedMap.get(STRING_KEY));

        typedMap.put(STRING_KEY, "Hello World!");
        Assertions.assertEquals("Hello World!", typedMap.get(STRING_KEY));

        typedMap.remove(STRING_KEY);
        Assertions.assertNull(typedMap.get(STRING_KEY));
    }

    @Test
    public void showString() {
        final TypedMap typedMap = new TypedMap();
        typedMap.put(STRING_KEY, "Hello World!");

        Assertions.assertEquals("{STRING_KEY=Hello World!}", typedMap.toString());
    }

    @Test
    public void equalsTypeMap() {
        final TypedMap typedMap1 = new TypedMap();
        typedMap1.put(STRING_KEY, "Hello World!");

        final TypedMap typedMap2 = new TypedMap();
        typedMap2.put(STRING_KEY, "Hello World!");

        Assertions.assertEquals(typedMap1, typedMap2);
        Assertions.assertEquals(typedMap1.hashCode(), typedMap2.hashCode());
    }

    @Test
    public void equalsKey() {
        final TypedMap.Key<String> key = TypedMap.Key.of("STRING_KEY");
        Assertions.assertEquals(STRING_KEY, key);
    }
}
