package dev.voidframework.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class IOUtilsTest {

    static Stream<Arguments> availableBytesNamedKeyValueArguments() {
        return Stream.of(
            Arguments.of(Named.of("Empty stream", InputStream.nullInputStream()), 0),
            Arguments.of(Named.of("Non empty stream", new ByteArrayInputStream("Hello World".getBytes(StandardCharsets.UTF_8))), 11),
            Arguments.of(Named.of("Null stream", null), -1));
    }

    @ParameterizedTest
    @MethodSource("availableBytesNamedKeyValueArguments")
    void availableBytes(final InputStream inputStream, final long expectedSize) {

        // Act
        final long availableBytes = IOUtils.availableBytes(inputStream);

        // Assert
        Assertions.assertEquals(expectedSize, availableBytes);
    }

    @Test
    void availableBytesWithException() throws IOException {

        // Arrange
        final InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(inputStream.available()).thenThrow(new IOException());

        // Act
        final long availableBytes = IOUtils.availableBytes(inputStream);

        // Assert
        Assertions.assertEquals(-1, availableBytes);
    }

    @Test
    void closeWithoutExceptionWithoutException() {

        // Arrange
        final InputStream inputStream = InputStream.nullInputStream();

        // Act + Assert
        Assertions.assertDoesNotThrow(() -> IOUtils.closeWithoutException(inputStream));
    }

    @Test
    void closeWithoutExceptionWithException() throws IOException {

        // Arrange
        final InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.doThrow(new IOException()).when(inputStream).close();

        // Act + Assert
        Assertions.assertDoesNotThrow(() -> IOUtils.closeWithoutException(inputStream));
    }

    @Test
    void constructor() throws NoSuchMethodException {

        // Act
        final Constructor<IOUtils> constructor = IOUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);

        // Assert
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertEquals("This is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    void resetWithoutExceptionWithoutException() throws IOException {

        // Arrange
        final InputStream inputStream = new ByteArrayInputStream("Hello World".getBytes(StandardCharsets.UTF_8));
        inputStream.readAllBytes();

        // Act
        final long availableBefore = IOUtils.availableBytes(inputStream);
        IOUtils.resetWithoutException(inputStream);
        final long availableAfter = IOUtils.availableBytes(inputStream);

        // Assert
        Assertions.assertEquals(0, availableBefore);
        Assertions.assertEquals(11, availableAfter);
    }

    @Test
    void resetWithoutExceptionWithoutExceptionNullStream() {

        // Act + Assert
        Assertions.assertDoesNotThrow(() -> IOUtils.resetWithoutException(null));
    }

    @Test
    void resetWithoutExceptionWithException() throws IOException {

        // Arrange
        final InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.doThrow(new IOException()).when(inputStream).close();

        // Act + Assert
        Assertions.assertDoesNotThrow(() -> IOUtils.resetWithoutException(inputStream));
    }
}
