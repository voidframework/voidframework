package dev.voidframework.core.remoteconfiguration;

import com.typesafe.config.Config;
import dev.voidframework.core.exception.RemoteConfigurationException;

import java.util.function.Consumer;

/**
 * Remote configuration provider.
 *
 * @since 1.2.0
 */
public interface RemoteConfigurationProvider {

    /**
     * Retrieves the provider name.
     *
     * @return The provider name
     * @since 1.2.0
     */
    String getName();

    /**
     * Retrieves the provider configuration object name.
     *
     * @return The provider configuration object name
     * @since 1.2.0
     */
    String getConfigurationObjectName();

    /**
     * Loads the configuration.
     *
     * @param configuration       The provider specific configuration
     * @param keyValueObjConsumer The Key/Value object consumer
     * @param fileObjConsumer     The File object consumer
     * @throws RemoteConfigurationException If something goes wrong during the process
     * @since 1.2.0
     */
    void loadConfiguration(final Config configuration,
                           final Consumer<KeyValueCfgObject> keyValueObjConsumer,
                           final Consumer<FileCfgObject> fileObjConsumer) throws RemoteConfigurationException;
}
