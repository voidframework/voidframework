package dev.voidframework.remoteconfiguration.provider;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.exception.RemoteConfigurationException;
import dev.voidframework.core.remoteconfiguration.AbstractRemoteConfigurationProvider;
import dev.voidframework.core.remoteconfiguration.FileCfgObject;
import dev.voidframework.core.remoteconfiguration.KeyValueCfgObject;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;

import java.nio.charset.StandardCharsets;
import java.security.ProviderException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Retrieves configuration from etcd.
 *
 * @since 1.2.0
 */
public class EtcdRemoteConfigurationProvider extends AbstractRemoteConfigurationProvider {

    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;
    private static final String USER_AGENT = "VoidFramework-RemoteConf-Provider";

    private static final String CONFIGURATION_KEY_ENDPOINT = "endpoint";
    private static final String CONFIGURATION_KEY_PREFIX = "prefix";
    private static final String CONFIGURATION_KEY_USERNAME = "username";
    private static final String CONFIGURATION_KEY_PASSWORD = "password";

    private static final String JSON_FIELD_NODE = "node";
    private static final String JSON_FIELD_NODES = "nodes";
    private static final String JSON_FIELD_IS_DIRECTORY = "dir";
    private static final String JSON_FIELD_DATA_KEY = "key";
    private static final String JSON_FIELD_DATA_VALUE = "value";

    @Override
    public String getName() {

        return "etcd";
    }

    @Override
    public String getConfigurationObjectName() {

        return "etcd";
    }

    @Override
    public void loadConfiguration(final Config configuration,
                                  final Consumer<KeyValueCfgObject> keyValueObjConsumer,
                                  final Consumer<FileCfgObject> fileObjConsumer) throws RemoteConfigurationException {

        String etcdEndpoint = configuration.getString(CONFIGURATION_KEY_ENDPOINT);
        String etcdPrefix = configuration.getString(CONFIGURATION_KEY_PREFIX);

        // Quick check of vital configuration keys
        if (etcdEndpoint == null) {
            throw new ConfigException.BadValue(configuration.origin(), CONFIGURATION_KEY_ENDPOINT, "Could not be null");
        } else if (!etcdEndpoint.startsWith("http")) {
            throw new ConfigException.BadValue(configuration.origin(), CONFIGURATION_KEY_ENDPOINT, "Must start with http:// or https://");
        } else if (etcdPrefix == null) {
            throw new ConfigException.BadValue(configuration.origin(), CONFIGURATION_KEY_PREFIX, "Could not be null");
        }

        // Normalize configuration values
        if (!etcdEndpoint.endsWith(StringConstants.SLASH)) {
            etcdEndpoint += StringConstants.SLASH;
        }
        if (etcdPrefix.endsWith(StringConstants.SLASH)) {
            etcdPrefix = etcdPrefix.substring(0, etcdPrefix.length() - 1);
        }
        if (etcdPrefix.startsWith(StringConstants.SLASH)) {
            etcdPrefix = etcdPrefix.substring(1);
        }

        // Get data from etcd
        try {

            final ClientBuilder clientBuilder = Client.builder().endpoints(etcdEndpoint);

            if (configuration.hasPath(CONFIGURATION_KEY_USERNAME)) {
                clientBuilder
                    .user(ByteSequence.from(configuration.getString(CONFIGURATION_KEY_USERNAME), StandardCharsets.UTF_8))
                    .password(ByteSequence.from(configuration.getString(CONFIGURATION_KEY_PASSWORD), StandardCharsets.UTF_8));
            }

            final GetResponse response = clientBuilder.build().getKVClient()
                .get(
                    ByteSequence.from(etcdPrefix, StandardCharsets.UTF_8),
                    GetOption.builder().isPrefix(true).build())
                .get();

            for (final KeyValue entry : response.getKvs()) {

                // Checks if current node is a directory
                final String cfgKey;
                if (etcdPrefix.isEmpty()) {
                    cfgKey = entry.getKey().toString(StandardCharsets.UTF_8)
                        .replace(StringConstants.SLASH, StringConstants.DOT);
                } else {
                    cfgKey = entry.getKey().toString(StandardCharsets.UTF_8)
                        .replace(etcdPrefix + StringConstants.SLASH, StringConstants.EMPTY)
                        .replace(StringConstants.SLASH, StringConstants.DOT);
                }

                // Checks if current configuration object is a file
                final String valueAsString = entry.getValue().toString(StandardCharsets.UTF_8);
                if (isFile(valueAsString)) {
                    fileObjConsumer.accept(
                        new FileCfgObject(cfgKey, valueAsString));
                } else {

                    // Standard configuration value
                    keyValueObjConsumer.accept(
                        new KeyValueCfgObject(cfgKey, valueAsString));
                }
            }

        } catch (final ExecutionException ex) {
            throw new ProviderException("Can't connect to the provider", ex);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ProviderException("Remote configuration provider has been interrupted", ex);
        }
    }
}
