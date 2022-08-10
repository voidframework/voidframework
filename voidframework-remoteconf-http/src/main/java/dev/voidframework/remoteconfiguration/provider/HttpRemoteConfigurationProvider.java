package dev.voidframework.remoteconfiguration.provider;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.exception.RemoteConfigurationException;
import dev.voidframework.core.remoteconfiguration.AbstractRemoteConfigurationProvider;
import dev.voidframework.core.remoteconfiguration.FileCfgObject;
import dev.voidframework.core.remoteconfiguration.KeyValueCfgObject;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Retrieves configuration from an HTTP server.
 */
public class HttpRemoteConfigurationProvider extends AbstractRemoteConfigurationProvider {

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
                    configuration.getString("endpoint"),
                    ex2.getCause().getClass().getName(),
                    ex2.getCause());
            } else {
                throw new ConfigException.ValidationFailed(
                    Collections.singletonList(
                        new ConfigException.ValidationProblem(
                            configuration.getString("endpoint"),
                            ex2.origin(),
                            ex2.getMessage())
                    )
                );
            }
        } catch (final MalformedURLException | UnknownHostException ex) {
            throw new ConfigException.BadValue("endpoint", ex.getMessage());
        } catch (final IOException ex) {
            throw new ConfigException.IO(configuration.origin(), ex.getMessage());
        }
    }

    /**
     * Retrieves the remote configuration.
     *
     * @param configuration The provider configuration
     * @return The remote configuration content as String
     * @throws IOException                             if something goes wrong
     * @throws RemoteConfigurationException.FetchError if the remote endpoint return an error
     */
    private String fetchRemoteConfiguration(final Config configuration) throws IOException {

        final URL url = new URL(configuration.getString("endpoint"));
        final HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

        if (configuration.hasPath("username")) {
            final String auth = configuration.getString("username") + ":" + configuration.getString("password");
            final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            httpConnection.setRequestProperty("Authorization", "Basic " + new String(encodedAuth));
        }

        final String httpMethod = configuration.getString("method").trim().toUpperCase(Locale.ENGLISH);
        httpConnection.setRequestMethod(httpMethod);
        if (httpMethod.equals("POST")) {
            httpConnection.setDoOutput(false);
        }

        httpConnection.setRequestProperty("User-Agent", "VoidFramework-RemoteConf-Provider");
        httpConnection.setConnectTimeout(5000);
        httpConnection.setReadTimeout(5000);
        httpConnection.connect();

        if (httpConnection.getResponseCode() / 100 == 2) {
            try (final BufferedReader br = new BufferedReader(
                new InputStreamReader(httpConnection.getInputStream(), StandardCharsets.UTF_8))) {

                final StringBuilder response = new StringBuilder();

                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                return response.toString();
            }
        }

        throw new RemoteConfigurationException.FetchError(
            this.getClass(),
            "Endpoint returns httpStatusCode " + httpConnection.getResponseCode());
    }
}
