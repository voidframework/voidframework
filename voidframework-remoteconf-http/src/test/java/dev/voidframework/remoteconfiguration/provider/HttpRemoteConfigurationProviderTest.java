package dev.voidframework.remoteconfiguration.provider;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.exception.RemoteConfigurationException;
import dev.voidframework.core.remoteconfiguration.FileCfgObject;
import dev.voidframework.core.remoteconfiguration.RemoteConfigurationProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class HttpRemoteConfigurationProviderTest {

    private static final Config LOCAL_CONFIGURATION = ConfigFactory.parseString("""
        endpoint = "https://pastebin.com/raw/dbcFZjDk"
        method = "GET"
        username = null
        password = null
        """);

    private static final Config LOCAL_CONFIGURATION_ERROR_FILE_NOT_FOUND = ConfigFactory.parseString("""
        endpoint = "https://pastebin.com/raw/00000000"
        method = "GET"
        """);

    private static final Config LOCAL_CONFIGURATION_ERROR_UNKNOWN_HOST = ConfigFactory.parseString("""
        endpoint = "https://domain-does-not-exist-424268efae4a4f0b8049be2dbe445c55.com/config"
        method = "GET"
        """);

    @Test
    void getName() {

        // Arrange
        final RemoteConfigurationProvider provider = new HttpRemoteConfigurationProvider();

        // Act
        final String providerName = provider.getName();

        // Assert
        Assertions.assertEquals("HTTP", providerName);
    }

    @Test
    void getConfigurationObjectName() {

        // Arrange
        final RemoteConfigurationProvider provider = new HttpRemoteConfigurationProvider();

        // Act
        final String configurationObjectName = provider.getConfigurationObjectName();

        // Assert
        Assertions.assertEquals("http", configurationObjectName);
    }

    @Test
    void remoteConfigurationFromHttp() {

        // Arrange
        final StringBuilder stringBuilder = new StringBuilder(512);
        final RemoteConfigurationProvider provider = new HttpRemoteConfigurationProvider();

        // Act
        provider.loadConfiguration(
            LOCAL_CONFIGURATION,
            keyValueCfgObject -> keyValueCfgObject.apply(stringBuilder),
            FileCfgObject::apply);

        final Config remoteConfig = ConfigFactory
            .parseString(stringBuilder.toString())
            .withFallback(LOCAL_CONFIGURATION);

        final File file = new File("./test");
        if (file.exists()) {
            file.deleteOnExit();
        }

        // Assert
        Assertions.assertEquals("Hello World", remoteConfig.getString("application.string"));
        Assertions.assertEquals(5, remoteConfig.getInt("application.integer"));
        Assertions.assertEquals(List.of(1, 2, 3, 4, 5), remoteConfig.getIntList("application.intlist"));
        Assertions.assertTrue(remoteConfig.getBoolean("application.boolean"));

        Assertions.assertTrue(file.exists());
        try (final InputStream initialStream = new FileInputStream(file)) {
            final byte[] buffer = new byte[128];
            final int nbRead = initialStream.read(buffer);
            buffer[nbRead] = '\0';

            Assertions.assertTrue(nbRead > 0);
            Assertions.assertEquals("Hello World!", new String(buffer, 0, nbRead));
        } catch (final IOException ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    void remoteConfigurationFromHttpErrorNotFound() {

        // Arrange
        final StringBuilder stringBuilder = new StringBuilder(512);
        final RemoteConfigurationProvider provider = new HttpRemoteConfigurationProvider();

        // Act & Assert
        Assertions.assertThrowsExactly(RemoteConfigurationException.FetchError.class, () -> provider.loadConfiguration(
            LOCAL_CONFIGURATION_ERROR_FILE_NOT_FOUND,
            keyValueCfgObject -> keyValueCfgObject.apply(stringBuilder),
            FileCfgObject::apply));
    }

    @Test
    void remoteConfigurationFromHttpErrorUnknownHost() {

        // Arrange
        final StringBuilder stringBuilder = new StringBuilder(512);
        final RemoteConfigurationProvider provider = new HttpRemoteConfigurationProvider();

        // Act & Assert
        Assertions.assertThrowsExactly(ConfigException.BadValue.class, () -> provider.loadConfiguration(
            LOCAL_CONFIGURATION_ERROR_UNKNOWN_HOST,
            keyValueCfgObject -> keyValueCfgObject.apply(stringBuilder),
            FileCfgObject::apply));
    }
}
