package dev.voidframework.remoteconfiguration.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import dev.voidframework.core.exception.RemoteConfigurationException;
import dev.voidframework.core.helper.IO;
import dev.voidframework.core.remoteconfiguration.AbstractRemoteConfigurationProvider;
import dev.voidframework.core.remoteconfiguration.FileCfgObject;
import dev.voidframework.core.remoteconfiguration.KeyValueCfgObject;
import org.apache.commons.lang3.RegExUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.ProviderException;
import java.util.Base64;
import java.util.function.Consumer;

/**
 * Retrieves configuration from etcd.
 */
public class EtcdRemoteConfigurationProvider extends AbstractRemoteConfigurationProvider {

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
        if (!etcdEndpoint.endsWith("/")) {
            etcdEndpoint += "/";
        }
        if (etcdPrefix.endsWith("/")) {
            etcdPrefix = etcdPrefix.substring(0, etcdPrefix.length() - 1);
        }
        if (etcdPrefix.startsWith("/")) {
            etcdPrefix = etcdPrefix.substring(1);
        }

        // Get data from etcd
        InputStream is = null;
        try {
            final URL consulUrl = new URL("%sv2/keys/%s/?recursive=true".formatted(etcdEndpoint, etcdPrefix));
            final HttpURLConnection conn = (HttpURLConnection) consulUrl.openConnection();

            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (configuration.hasPath(CONFIGURATION_KEY_USERNAME)
                && configuration.hasPath(CONFIGURATION_KEY_PASSWORD)) {
                final String username = configuration.getString(CONFIGURATION_KEY_USERNAME);
                final String password = configuration.getString(CONFIGURATION_KEY_PASSWORD);
                if (!username.isEmpty()) {
                    final String basicAuth = "Basic " + Base64.getEncoder().encodeToString(
                        (username + ":" + password).getBytes()
                    );
                    conn.setRequestProperty("Authorization", basicAuth);
                }
            }

            if (conn.getResponseCode() / 100 == 2) {
                is = conn.getInputStream();
                final ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(is).get(JSON_FIELD_NODE);
                if (jsonNode.get(JSON_FIELD_IS_DIRECTORY).asBoolean()) {
                    jsonNode = jsonNode.get(JSON_FIELD_NODES);

                    // Explore Json
                    this.exploreJsonNode(etcdPrefix, jsonNode, keyValueObjConsumer, fileObjConsumer);
                } else {
                    throw new ConfigException.BadValue(CONFIGURATION_KEY_PREFIX, "Must reference a directory");
                }
            } else {
                throw new ProviderException("Return non 200 status: " + conn.getResponseCode());
            }
        } catch (final MalformedURLException ex) {
            throw new ConfigException.BadValue(CONFIGURATION_KEY_ENDPOINT, ex.getMessage());
        } catch (final IOException ex) {
            throw new ProviderException("Can't connect to the provider", ex);
        } finally {
            IO.closeWithoutException(is);
        }
    }

    /**
     * Explore the Json document to retrieve valid data.
     *
     * @param prefix              The key prefix to remove
     * @param jsonNode            The Json node to explore
     * @param keyValueObjConsumer The Key/Value object consumer
     * @param fileObjConsumer     The File object consumer
     */
    private void exploreJsonNode(final String prefix,
                                 final JsonNode jsonNode,
                                 final Consumer<KeyValueCfgObject> keyValueObjConsumer,
                                 final Consumer<FileCfgObject> fileObjConsumer) {

        for (final JsonNode entry : jsonNode) {

            // Check if current node is a directory
            if (entry.hasNonNull(JSON_FIELD_IS_DIRECTORY) && entry.get(JSON_FIELD_IS_DIRECTORY).asBoolean()) {
                this.exploreJsonNode(prefix, entry.get(JSON_FIELD_NODES), keyValueObjConsumer, fileObjConsumer);
            } else if (entry.hasNonNull(JSON_FIELD_DATA_VALUE)) {

                // Process current configuration object
                final String cfgKey;
                if (prefix.isEmpty()) {
                    cfgKey = RegExUtils.removeFirst(
                            entry.get(JSON_FIELD_DATA_KEY).asText(),
                            "/")
                        .replace("/", ".");
                } else {
                    cfgKey = entry.get(JSON_FIELD_DATA_KEY)
                        .asText()
                        .replace("/" + prefix + "/", "")
                        .replace("/", ".");
                }

                // Check if current configuration object is a file
                if (isFile(entry.get(JSON_FIELD_DATA_VALUE).asText())) {
                    fileObjConsumer.accept(
                        new FileCfgObject(cfgKey, entry.get(JSON_FIELD_DATA_VALUE).asText()));
                } else {

                    // Standard configuration value
                    keyValueObjConsumer.accept(
                        new KeyValueCfgObject(cfgKey, entry.get(JSON_FIELD_DATA_VALUE).asText()));
                }
            }
        }
    }
}
