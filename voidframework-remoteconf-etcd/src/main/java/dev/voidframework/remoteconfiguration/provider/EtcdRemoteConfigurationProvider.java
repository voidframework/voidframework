package dev.voidframework.remoteconfiguration.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import dev.voidframework.core.exception.RemoteConfigurationException;
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

        String etcdEndpoint = configuration.getString("endpoint");
        String etcdPrefix = configuration.getString("prefix");

        // Quick check of vital configuration keys
        if (etcdEndpoint == null) {
            throw new ConfigException.BadValue(configuration.origin(), "endpoint", "Could not be null");
        } else if (!etcdEndpoint.startsWith("http")) {
            throw new ConfigException.BadValue(configuration.origin(), "endpoint", "Must start with http:// or https://");
        } else if (etcdPrefix == null) {
            throw new ConfigException.BadValue(configuration.origin(), "prefix", "Could not be null");
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

            if (configuration.hasPath("username")
                && configuration.hasPath("password")) {
                final String username = configuration.getString("username");
                final String password = configuration.getString("password");
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
                JsonNode jsonNode = mapper.readTree(is).get("node");
                if (jsonNode.get("dir").asBoolean()) {
                    jsonNode = jsonNode.get("nodes");

                    // Explore Json
                    this.exploreJsonNode(etcdPrefix, jsonNode, keyValueObjConsumer, fileObjConsumer);
                } else {
                    throw new ConfigException.BadValue("prefix", "Must reference a directory");
                }
            } else {
                throw new ProviderException("Return non 200 status: " + conn.getResponseCode());
            }
        } catch (final MalformedURLException ex) {
            throw new ConfigException.BadValue("endpoint", ex.getMessage());
        } catch (final IOException ex) {
            throw new ProviderException("Can't connect to the provider", ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (final IOException ignore) {
                }
            }
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
            if (entry.hasNonNull("dir") && entry.get("dir").asBoolean()) {
                this.exploreJsonNode(prefix, entry.get("nodes"), keyValueObjConsumer, fileObjConsumer);
            } else if (entry.hasNonNull("value")) {

                // Process current configuration object
                final String cfgKey;
                if (prefix.isEmpty()) {
                    cfgKey = RegExUtils.removeFirst(
                            entry.get("key").asText(),
                            "/")
                        .replace("/", ".");
                } else {
                    cfgKey = entry.get("key")
                        .asText()
                        .replace("/" + prefix + "/", "")
                        .replace("/", ".");
                }

                // Check if current configuration object is a file
                if (isFile(entry.get("value").asText())) {
                    fileObjConsumer.accept(
                        new FileCfgObject(cfgKey, entry.get("value").asText()));
                } else {

                    // Standard configuration value
                    keyValueObjConsumer.accept(
                        new KeyValueCfgObject(cfgKey, entry.get("value").asText()));
                }
            }
        }
    }
}
