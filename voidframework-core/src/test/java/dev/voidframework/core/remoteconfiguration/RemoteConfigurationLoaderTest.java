package dev.voidframework.core.remoteconfiguration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class RemoteConfigurationLoaderTest {

    @Test
    void processAllProvidersWithSingleProvider() throws IOException {

        // Arrange
        final Config localConfiguration = ConfigFactory.parseString("""
            voidframework.core.remoteConfiguration.providers = \
                "dev.voidframework.core.remoteconfiguration.provider.DummyRemoteConfigurationProvider"

            voidframework.core.remoteConfiguration.dummy {
            }
            """);

        // Act
        final Config remoteConfiguration = RemoteConfigurationLoader.processAllProviders(localConfiguration);

        final File file = new File("./test");
        if (file.exists()) {
            file.deleteOnExit();
        }

        // Assert
        Assertions.assertNotNull(remoteConfiguration);
        Assertions.assertEquals(2, remoteConfiguration.entrySet().size());
        Assertions.assertEquals("Hello World!", remoteConfiguration.getString("cfg.string"));
        Assertions.assertTrue(remoteConfiguration.getBoolean("cfg.boolean"));

        final byte[] readedContentAsByteArray = Files.readAllBytes(file.toPath());
        final String readedContent = new String(readedContentAsByteArray, StandardCharsets.UTF_8);
        Assertions.assertEquals("Hello World!", readedContent);
    }

    @Test
    void processAllProvidersWithListOfProviders() throws IOException {

        // Arrange
        final Config localConfiguration = ConfigFactory.parseString("""
            voidframework.core.remoteConfiguration.providers = \
                ["dev.voidframework.core.remoteconfiguration.provider.DummyRemoteConfigurationProvider"]

            voidframework.core.remoteConfiguration.dummy {
            }
            """);

        // Act
        final Config remoteConfiguration = RemoteConfigurationLoader.processAllProviders(localConfiguration);

        final File file = new File("./test");
        if (file.exists()) {
            file.deleteOnExit();
        }

        // Assert
        Assertions.assertNotNull(remoteConfiguration);
        Assertions.assertEquals(2, remoteConfiguration.entrySet().size());
        Assertions.assertEquals("Hello World!", remoteConfiguration.getString("cfg.string"));
        Assertions.assertTrue(remoteConfiguration.getBoolean("cfg.boolean"));

        final byte[] readedContentAsByteArray = Files.readAllBytes(file.toPath());
        final String readedContent = new String(readedContentAsByteArray, StandardCharsets.UTF_8);
        Assertions.assertEquals("Hello World!", readedContent);
    }
}
