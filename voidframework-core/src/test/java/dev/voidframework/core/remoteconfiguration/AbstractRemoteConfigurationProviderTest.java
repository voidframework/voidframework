package dev.voidframework.core.remoteconfiguration;

import dev.voidframework.core.remoteconfiguration.provider.DummyRemoteConfigurationProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class AbstractRemoteConfigurationProviderTest {

    @Test
    void isFile() {

        // Arrange
        final AbstractRemoteConfigurationProvider provider = new DummyRemoteConfigurationProvider();

        // Act
        final boolean isFile = provider.isFile("<FILE>./test;SGVsbG8gV29ybGQh");

        // Assert
        Assertions.assertTrue(isFile);
    }

    @Test
    void isFileQuoted() {

        // Arrange
        final AbstractRemoteConfigurationProvider provider = new DummyRemoteConfigurationProvider();

        // Act
        final boolean isFile = provider.isFile("\"<FILE>./test;SGVsbG8gV29ybGQh\"");

        // Assert
        Assertions.assertTrue(isFile);
    }

    @Test
    void isFileNotAFile() {

        // Arrange
        final AbstractRemoteConfigurationProvider provider = new DummyRemoteConfigurationProvider();

        // Act
        final boolean isFile = provider.isFile("Hello World!");

        // Assert
        Assertions.assertFalse(isFile);
    }
}
