package dev.voidframework.remoteconfiguration.provider;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.exception.RemoteConfigurationException;
import dev.voidframework.core.remoteconfiguration.AbstractRemoteConfigurationProvider;
import dev.voidframework.core.remoteconfiguration.FileCfgObject;
import dev.voidframework.core.remoteconfiguration.KeyValueCfgObject;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
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
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Retrieves configuration from an HTTP server.
 *
 * @since 1.2.0
 */
public class HttpRemoteConfigurationProvider extends AbstractRemoteConfigurationProvider {

    private static final String CONFIGURATION_KEY_ENDPOINT = "endpoint";
    private static final String CONFIGURATION_KEY_METHOD = "method";
    private static final String CONFIGURATION_KEY_USERNAME = "username";
    private static final String CONFIGURATION_KEY_PASSWORD = "password";

    private static final String USER_AGENT = "VoidFramework-RemoteConf-Provider";
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    @Override
    public String getName() {

        return "HTTP";
    }

    @Override
    public String getConfigurationObjectName() {

        return "http";
    }

    @Override
    public void loadConfiguration(final Config configuration,
                                  final Consumer<KeyValueCfgObject> keyValueObjConsumer,
                                  final Consumer<FileCfgObject> fileObjConsumer) throws RemoteConfigurationException {

        try {
            final String remoteConfigurationContent = fetchRemoteConfiguration(configuration);
            if (StringUtils.isBlank(remoteConfigurationContent)) {
                return;
            }

            final Config remoteConfiguration = ConfigFactory.parseString(remoteConfigurationContent);

            remoteConfiguration.entrySet().forEach(entry -> {
                final String value = entry.getValue().render();
                if (isFile(value)) {
                    fileObjConsumer.accept(
                        new FileCfgObject(entry.getKey(), value));
                } else {
                    keyValueObjConsumer.accept(
                        new KeyValueCfgObject(entry.getKey(), value));
                }
            });
        } catch (final ConfigException ex2) {
            if (ex2.getCause() != null) {
                throw new ConfigException.BadPath(
                    configuration.getString(CONFIGURATION_KEY_ENDPOINT),
                    ex2.getCause().getClass().getName(),
                    ex2.getCause());
            } else {
                throw new ConfigException.ValidationFailed(
                    Collections.singletonList(
                        new ConfigException.ValidationProblem(
                            configuration.getString(CONFIGURATION_KEY_ENDPOINT),
                            ex2.origin(),
                            ex2.getMessage())
                    )
                );
            }
        } catch (final ConnectException | MalformedURLException | UnknownHostException ex) {
            throw new ConfigException.BadValue(CONFIGURATION_KEY_ENDPOINT, ex.getMessage());
        } catch (final IOException ex) {
            throw new ConfigException.IO(configuration.origin(), ex.getMessage());
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ProviderException("Remote configuration provider has been interrupted", ex);
        }
    }

    /**
     * Retrieves the remote configuration.
     *
     * @param configuration The provider configuration
     * @return The remote configuration content as String
     * @throws InterruptedException                    if execution is interrupted
     * @throws IOException                             if something goes wrong
     * @throws RemoteConfigurationException.FetchError if the remote endpoint return an error
     * @since 1.2.0
     */
    private String fetchRemoteConfiguration(final Config configuration) throws InterruptedException, IOException {

        // Creates HTTP client
        final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofMillis(CONNECTION_TIMEOUT))
            .executor(Executors.newSingleThreadExecutor())
            .build();

        // Creates HTTP request
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(configuration.getString(CONFIGURATION_KEY_ENDPOINT)))
            .timeout(Duration.ofMillis(READ_TIMEOUT))
            .setHeader("User-Agent", USER_AGENT);

        final String httpMethod = configuration.getString(CONFIGURATION_KEY_METHOD).trim().toUpperCase(Locale.ENGLISH);
        if (httpMethod.equals("POST")) {
            requestBuilder.POST(HttpRequest.BodyPublishers.noBody());
        } else {
            requestBuilder.GET();
        }

        if (configuration.hasPath(CONFIGURATION_KEY_USERNAME)) {
            final String auth = configuration.getString(CONFIGURATION_KEY_USERNAME)
                + StringConstants.COLON
                + configuration.getString(CONFIGURATION_KEY_PASSWORD);
            final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            requestBuilder.setHeader("Authorization", "Basic " + new String(encodedAuth));
        }

        // Executes HTTP request
        final HttpResponse<String> httpResponse = httpClient.send(
            requestBuilder.build(),
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        // Uses response as configuration content
        if (httpResponse.statusCode() / 100 == 2) {
            return httpResponse.body();
        }

        // Throws exception because endpoint return a non 2xx response
        throw new RemoteConfigurationException.FetchError(
            this.getClass(),
            "Endpoint returns httpStatusCode " + httpResponse.statusCode());
    }
}
