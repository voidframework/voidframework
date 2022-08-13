package dev.voidframework.core.remoteconfiguration.provider;

import com.typesafe.config.Config;
import dev.voidframework.core.exception.RemoteConfigurationException;
import dev.voidframework.core.remoteconfiguration.AbstractRemoteConfigurationProvider;
import dev.voidframework.core.remoteconfiguration.FileCfgObject;
import dev.voidframework.core.remoteconfiguration.KeyValueCfgObject;

import java.util.function.Consumer;

/**
 * Dummy remote configuration provider.
 */
public class DummyRemoteConfigurationProvider extends AbstractRemoteConfigurationProvider {

    @Override
    public String getName() {

        return "Dummy";
    }

    @Override
    public String getConfigurationObjectName() {

        return "dummy";
    }

    @Override
    public void loadConfiguration(final Config configuration,
                                  final Consumer<KeyValueCfgObject> keyValueObjConsumer,
                                  final Consumer<FileCfgObject> fileObjConsumer) throws RemoteConfigurationException {

        keyValueObjConsumer.accept(
            new KeyValueCfgObject("cfg.string", "Hello World!"));

        keyValueObjConsumer.accept(
            new KeyValueCfgObject("cfg.boolean", "true"));

        fileObjConsumer.accept(
            new FileCfgObject("cfg.file", "<FILE>./test;SGVsbG8gV29ybGQh"));
    }
}
