package dev.voidframework.core.utils;

import com.esotericsoftware.kryo.Kryo;
import dev.voidframework.core.lang.CUID;
import dev.voidframework.core.lang.Either;
import dev.voidframework.core.lang.TypedMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class KryoUtilsTest {

    static Stream<Arguments> getDeserializeArguments() {
        return Stream.of(
            Arguments.of(new byte[]{1, -14, 20}, Integer.class, 1337),
            Arguments.of(new byte[]{72, 101, 108, 108, 111, 32, 87, 111, 114, 108, -28}, String.class, "Hello World"),
            Arguments.of(new byte[]{1, 12, 2, 10, 0}, Optional.class, Optional.of(BigDecimal.TEN)),
            Arguments.of(new byte[]{1, 12, 3, 8, 0, 0}, Optional.class, Optional.of(BigDecimal.valueOf(2048))),
            Arguments.of(new byte[]{1, 2, 3, 65, 112, 112, 108, -27, 3, 66, 97, 110, 97, 110, -31}, List.class, List.of("Apple", "Banana")),
            Arguments.of(new byte[]{1, 1, 3, 65, 112, 112, 108, -27}, Set.class, Set.of("Apple")),
            Arguments.of(new byte[]{1, 3, 76, 69, 70, -44, 0}, Either.class, Either.ofLeft("LEFT")),
            Arguments.of(new byte[]{1, 0, 3, 82, 73, 71, 72, -44}, Either.class, Either.ofRight("RIGHT")),
            Arguments.of(new byte[]{1, 99, 108, 57, 103, 116, 115, 49, 107, 119, 48, 48, 51, 57, 51, 54, 52, 55, 119, 49, 122, 52, 118, 50, 116, -29},
                CUID.class,
                CUID.fromString("cl9gts1kw00393647w1z4v2tc")),
            Arguments.of(new byte[0], String.class, null),
            Arguments.of(new byte[]{0}, Set.class, null));
    }

    static Stream<Arguments> getDeserializeWithoutExceptionArguments() {
        return Stream.of(
            Arguments.of(new byte[]{1, -14, 20}, Integer.class, 1337),
            Arguments.of(new byte[]{72, 101, 108, 108, 111, 32, 87, 111, 114, 108, -28}, String.class, "Hello World"),
            Arguments.of(new byte[]{1, 12, 2, 10, 0}, Optional.class, Optional.of(BigDecimal.TEN)),
            Arguments.of(new byte[]{1, 12, 3, 8, 0, 0}, Optional.class, Optional.of(BigDecimal.valueOf(2048))),
            Arguments.of(new byte[]{1, 12, 3, 8, 0, 0}, TestDTO.class, null), // "null" because no serializer registered for "TestDTO"
            Arguments.of(new byte[]{1, 2, 3, 65, 112, 112, 108, -27, 3, 66, 97, 110, 97, 110, -31}, List.class, List.of("Apple", "Banana")),
            Arguments.of(new byte[]{1, 1, 3, 65, 112, 112, 108, -27}, Set.class, Set.of("Apple")),
            Arguments.of(new byte[]{1, 3, 76, 69, 70, -44, 0}, Either.class, Either.ofLeft("LEFT")),
            Arguments.of(new byte[]{1, 0, 3, 82, 73, 71, 72, -44}, Either.class, Either.ofRight("RIGHT")),
            Arguments.of(new byte[]{1, 99, 108, 57, 103, 116, 115, 49, 107, 119, 48, 48, 51, 57, 51, 54, 52, 55, 119, 49, 122, 52, 118, 50, 116, -29},
                CUID.class,
                CUID.fromString("cl9gts1kw00393647w1z4v2tc")),
            Arguments.of(new byte[0], String.class, null),
            Arguments.of(new byte[]{0}, Set.class, null));
    }

    static Stream<Arguments> getSerializeArguments() {
        return Stream.of(
            Arguments.of(1337, new byte[]{1, -14, 20}),
            Arguments.of("Hello World", new byte[]{72, 101, 108, 108, 111, 32, 87, 111, 114, 108, -28}),
            Arguments.of(Optional.of(BigDecimal.TEN), new byte[]{1, 12, 2, 10, 0}),
            Arguments.of(Optional.of(BigDecimal.valueOf(2048)), new byte[]{1, 12, 3, 8, 0, 0}),
            Arguments.of(new TestDTO("Clémence"), new byte[]{1, -119, 67, 108, -61, -87, 109, 101, 110, 99, 101}),
            Arguments.of(CUID.fromString("cl9gts1kw00393647w1z4v2tc"),
                new byte[]{1, 99, 108, 57, 103, 116, 115, 49, 107, 119, 48, 48, 51, 57, 51, 54, 52, 55, 119, 49, 122, 52, 118, 50, 116, -29}),
            Arguments.of(Either.ofLeft("LEFT"), new byte[]{1, 3, 76, 69, 70, -44, 0}),
            Arguments.of(Either.ofRight("RIGHT"), new byte[]{1, 0, 3, 82, 73, 71, 72, -44}),
            Arguments.of(List.of("Apple", "Banana"), new byte[]{1, 2, 3, 65, 112, 112, 108, -27, 3, 66, 97, 110, 97, 110, -31}),
            Arguments.of(Set.of("Apple"), new byte[]{1, 1, 3, 65, 112, 112, 108, -27}),
            Arguments.of(null, new byte[]{0}));
    }

    @Test
    void constructor() throws NoSuchMethodException {

        // Act
        final Constructor<KryoUtils> constructor = KryoUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);

        // Assert
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertEquals("This is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    void kryo() {

        // Act
        final Kryo kryo = KryoUtils.kryo();

        // Assert
        Assertions.assertNotNull(kryo);
    }

    @ParameterizedTest
    @MethodSource("getDeserializeArguments")
    void deserialize(final byte[] toDeserialize, final Class<?> outputClassType, final Object expected) {

        // Act
        final Object deserializedObject = KryoUtils.deserialize(toDeserialize, outputClassType);

        // Assert
        Assertions.assertEquals(expected, deserializedObject);
    }

    @ParameterizedTest
    @MethodSource("getDeserializeWithoutExceptionArguments")
    void deserializeWithoutException(final byte[] toDeserialize, final Class<?> outputClassType, final Object expected) {

        // Act
        final Object deserializedObject = KryoUtils.deserializeWithoutException(toDeserialize, outputClassType);

        // Assert
        Assertions.assertEquals(expected, deserializedObject);
    }

    @ParameterizedTest
    @MethodSource("getSerializeArguments")
    void serialize(final Object toSerialize, final byte[] expected) {

        // Act
        final byte[] serializedContent = KryoUtils.serialize(toSerialize);

        // Assert
        Assertions.assertArrayEquals(expected, serializedContent);
    }

    @ParameterizedTest
    @MethodSource("getSerializeArguments")
    void serializeWithoutException(final Object toSerialize, final byte[] expected) {

        // Act
        final byte[] serializedContent = KryoUtils.serializeWithoutException(toSerialize);

        // Assert
        Assertions.assertArrayEquals(expected, serializedContent);
    }

    @Test
    void serializeAndDeserializeTypedMap() {

        // Arrange
        final TypedMap.Key<String> token = TypedMap.Key.of("TOKEN", String.class);
        final TypedMap.Key<Integer> birthYear = TypedMap.Key.of("B_YEAR", Integer.class);

        final TypedMap typedMap = new TypedMap();
        typedMap.put(token, "d0944b7c-b01f-4be7-9ace-155927589a8b");
        typedMap.put(birthYear, 1742);

        // Act
        final byte[] serializedContent = KryoUtils.serialize(typedMap);
        final TypedMap typedMapDeserialized = KryoUtils.deserialize(serializedContent, TypedMap.class);

        // Assert
        Assertions.assertEquals(typedMap, typedMapDeserialized);
    }

    /**
     * Test DTO.
     */
    public static final class TestDTO {

        final String name;

        public TestDTO(final String name) {

            this.name = name;
        }
    }
}
