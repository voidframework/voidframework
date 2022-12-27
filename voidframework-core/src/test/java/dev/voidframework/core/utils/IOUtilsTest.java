package dev.voidframework.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class IOUtilsTest {

    static Stream<Arguments> availableBytesNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("Empty stream", InputStream.nullInputStream()), 0),
            Arguments.of(Named.of("Non empty stream", new ByteArrayInputStream("Hello World".getBytes(StandardCharsets.UTF_8))), 11));
    }

    @ParameterizedTest
    @MethodSource("availableBytesNamedKeyValueArguments")
    void availableBytes(final InputStream inputStream, final long expectedSize) {

        // Act
        final long availableBytes = IOUtils.availableBytes(inputStream);

        // Assert
        Assertions.assertEquals(expectedSize, availableBytes);
    }
}
