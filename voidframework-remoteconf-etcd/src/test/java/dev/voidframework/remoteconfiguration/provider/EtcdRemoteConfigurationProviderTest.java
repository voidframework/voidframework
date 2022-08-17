package dev.voidframework.remoteconfiguration.provider;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
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
public class EtcdRemoteConfigurationProviderTest {

    @Test
    void getName() {

        // Arrange
        final RemoteConfigurationProvider provider = new EtcdRemoteConfigurationProvider();

        // Act
        final String providerName = provider.getName();

        // Assert
        Assertions.assertEquals("etcd", providerName);
    }

    @Test
    void getConfigurationObjectName() {

        // Arrange
        final RemoteConfigurationProvider provider = new EtcdRemoteConfigurationProvider();

        // Act
        final String configurationObjectName = provider.getConfigurationObjectName();

        // Assert
        Assertions.assertEquals("etcd", configurationObjectName);
    }

    @Test
    void remoteConfigurationFromEtcd() {

        // Arrange
        final StringBuilder stringBuilder = new StringBuilder(512);
        final RemoteConfigurationProvider provider = new EtcdRemoteConfigurationProvider();
        final Config localConfiguration = ConfigFactory.parseString("""
            endpoint = "http://127.0.0.1:2379"
            password = "insecurepassword"
            prefix = "cfg"
            """);

        // Act
        provider.loadConfiguration(
            localConfiguration,
            keyValueCfgObject -> keyValueCfgObject.apply(stringBuilder),
            FileCfgObject::apply);

        final Config remoteConfig = ConfigFactory
            .parseString(stringBuilder.toString())
            .withFallback(localConfiguration);

        final File file = new File("./test");
        if (file.exists()) {
            file.deleteOnExit();
        }

        // Assert
        Assertions.assertEquals("Hello World", remoteConfig.getString("string"));
        Assertions.assertEquals(1, remoteConfig.getInt("integer"));
        Assertions.assertEquals(List.of(1, 2, 3, 4, 5), remoteConfig.getIntList("intlist"));
        Assertions.assertTrue(remoteConfig.getBoolean("boolean"));

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
}
