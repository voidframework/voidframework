package dev.voidframework.remoteconfiguration.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.exception.RemoteConfigurationException;
import dev.voidframework.core.remoteconfiguration.AbstractRemoteConfigurationProvider;
import dev.voidframework.core.remoteconfiguration.FileCfgObject;
import dev.voidframework.core.remoteconfiguration.KeyValueCfgObject;
import dev.voidframework.core.utils.IOUtils;
import org.apache.commons.lang3.RegExUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.ProviderException;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.Executors;
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
        InputStream is = null;
        try {

            final URI remoteConfURI = URI.create("%sv2/keys/%s/?recursive=true".formatted(etcdEndpoint, etcdPrefix));
            is = fetchRemoteConfiguration(remoteConfURI, configuration);

            final ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(is).get(JSON_FIELD_NODE);
            if (jsonNode.get(JSON_FIELD_IS_DIRECTORY).asBoolean()) {
                jsonNode = jsonNode.get(JSON_FIELD_NODES);

                // Explore Json
                this.exploreJsonNode(etcdPrefix, jsonNode, keyValueObjConsumer, fileObjConsumer);
            } else {
                throw new ConfigException.BadValue(CONFIGURATION_KEY_PREFIX, "Must reference a directory");
            }

        } catch (final ConnectException | MalformedURLException | UnknownHostException ex) {
            throw new ConfigException.BadValue(CONFIGURATION_KEY_ENDPOINT, ex.getMessage());
        } catch (final IOException ex) {
            throw new ProviderException("Can't connect to the provider", ex);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ProviderException("Remote configuration provider has been interrupted", ex);
        } finally {
            IOUtils.closeWithoutException(is);
        }
    }

    /**
     * Retrieves the remote configuration.
     *
     * @param uri           The remote URI to fetch
     * @param configuration The application configuration
     * @throws InterruptedException                    if execution is interrupted
     * @throws IOException                             if something goes wrong
     * @throws RemoteConfigurationException.FetchError if the remote endpoint return an error
     * @since 1.7.0
     */
    private InputStream fetchRemoteConfiguration(final URI uri, final Config configuration) throws IOException, InterruptedException {

        // Creates HTTP client
        final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofMillis(CONNECTION_TIMEOUT))
            .executor(Executors.newSingleThreadExecutor())
            .build();

        // Creates HTTP request
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofMillis(READ_TIMEOUT))
            .setHeader("User-Agent", USER_AGENT)
            .GET();

        if (configuration.hasPath(CONFIGURATION_KEY_USERNAME)) {
            final String auth = configuration.getString(CONFIGURATION_KEY_USERNAME)
                + StringConstants.COLON
                + configuration.getString(CONFIGURATION_KEY_PASSWORD);
            final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            requestBuilder.setHeader("Authorization", "Basic " + new String(encodedAuth));
        }

        // Executes HTTP request
        final HttpResponse<InputStream> httpResponse = httpClient.send(
            requestBuilder.build(),
            HttpResponse.BodyHandlers.ofInputStream());

        // Uses response as configuration content
        if (httpResponse.statusCode() / 100 == 2) {
            return httpResponse.body();
        }

        // Throws exception because endpoint return a non 2xx response
        throw new RemoteConfigurationException.FetchError(
            this.getClass(),
            "Endpoint returns httpStatusCode " + httpResponse.statusCode());
    }

    /**
     * Explore the Json document to retrieve valid data.
     *
     * @param prefix              The key prefix to remove
     * @param jsonNode            The Json node to explore
     * @param keyValueObjConsumer The Key/Value object consumer
     * @param fileObjConsumer     The File object consumer
     * @since 1.2.0
     */
    private void exploreJsonNode(final String prefix,
                                 final JsonNode jsonNode,
                                 final Consumer<KeyValueCfgObject> keyValueObjConsumer,
                                 final Consumer<FileCfgObject> fileObjConsumer) {

        for (final JsonNode entry : jsonNode) {

            // Checks if current node is a directory
            if (entry.hasNonNull(JSON_FIELD_IS_DIRECTORY) && entry.get(JSON_FIELD_IS_DIRECTORY).asBoolean()) {
                this.exploreJsonNode(prefix, entry.get(JSON_FIELD_NODES), keyValueObjConsumer, fileObjConsumer);
            } else if (entry.hasNonNull(JSON_FIELD_DATA_VALUE)) {

                // Process current configuration object
                final String cfgKey;
                if (prefix.isEmpty()) {
                    cfgKey = RegExUtils.removeFirst(
                            entry.get(JSON_FIELD_DATA_KEY).asText(),
                            StringConstants.SLASH)
                        .replace(StringConstants.SLASH, StringConstants.DOT);
                } else {
                    cfgKey = entry.get(JSON_FIELD_DATA_KEY)
                        .asText()
                        .replace(StringConstants.SLASH + prefix + StringConstants.SLASH, StringConstants.EMPTY)
                        .replace(StringConstants.SLASH, StringConstants.DOT);
                }

                // Checks if current configuration object is a file
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
