package dev.voidframework.restclient.module;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import dev.voidframework.core.classestoload.ScannedClassesToLoad;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.utils.ConfigurationUtils;
import dev.voidframework.core.utils.JsonUtils;
import dev.voidframework.core.utils.XmlUtils;
import dev.voidframework.core.utils.YamlUtils;
import dev.voidframework.restclient.annotation.RestClient;
import dev.voidframework.restclient.retrofit.CallAdapterFactory;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * REST Client module.
 *
 * @since 1.9.0
 */
public final class RestClientModule extends AbstractModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientModule.class);

    private final Config configuration;
    private final ScannedClassesToLoad scannedClassesToLoad;

    /**
     * Build a new instance.
     *
     * @param configuration        The application configuration
     * @param scannedClassesToLoad The scanned classes
     * @since 1.9.0
     */
    public RestClientModule(final Config configuration, final ScannedClassesToLoad scannedClassesToLoad) {

        this.configuration = configuration;
        this.scannedClassesToLoad = scannedClassesToLoad;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void configure() {

        // Creates REST proxy
        for (final Class<?> proxyable : this.scannedClassesToLoad.proxyableList()) {
            final RestClient restClient = proxyable.getAnnotation(RestClient.class);
            if (restClient != null) {
                // Retrieves configuration key prefix
                final String restClientConfigurationKeyPrefix = this.retrieveConfigurationKeyPrefix(restClient);

                // Creates HTTP Client
                final long keepAliveDurationAsMilliseconds = ConfigurationUtils.getDurationOrFallback(
                    this.configuration,
                    restClientConfigurationKeyPrefix + ".keepAliveDuration",
                    TimeUnit.MILLISECONDS,
                    "voidframework.restclient.keepAliveDuration");
                final ConnectionPool connectionPool = new ConnectionPool(
                    ConfigurationUtils.getIntOrFallback(
                        this.configuration,
                        restClientConfigurationKeyPrefix + ".maxIdleConnections",
                        "voidframework.restclient.maxIdleConnections"),
                    keepAliveDurationAsMilliseconds,
                    TimeUnit.MILLISECONDS);
                final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(
                        ConfigurationUtils.getDurationOrFallback(
                            this.configuration,
                            restClientConfigurationKeyPrefix + ".readTimeout",
                            "voidframework.restclient.readTimeout"))
                    .connectTimeout(
                        ConfigurationUtils.getDurationOrFallback(
                            this.configuration,
                            restClientConfigurationKeyPrefix + ".connectionTimeout",
                            "voidframework.restclient.connectionTimeout"))
                    .connectionPool(connectionPool)
                    .build();

                // Creates proxy instance
                final Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .addCallAdapterFactory(new CallAdapterFactory())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(JacksonConverterFactory.create(JsonUtils.objectMapper()))
                    .addConverterFactory(JacksonConverterFactory.create(XmlUtils.objectMapper()))
                    .addConverterFactory(JacksonConverterFactory.create(YamlUtils.objectMapper()))
                    .baseUrl(this.configuration.getString(restClientConfigurationKeyPrefix + ".endpoint"))
                    .build();

                final Object proxyInstance = retrofit.create(proxyable);
                bind((Class) proxyable).toInstance(proxyInstance);

                LOGGER.info("REST Client proxy created for '{}'", proxyable.getName());
            }
        }
    }

    /**
     * Retrieves REST Client configuration key prefix.
     *
     * @param restClient REST Client annotation
     * @return REST Client configuration key prefix
     * @since 1.9.0
     */
    private String retrieveConfigurationKeyPrefix(final RestClient restClient) {

        if (StringUtils.isBlank(restClient.value())) {
            throw new RuntimeException("OOOOOOOPPPPPSSSSS"); // TODO: use dedicated exception
        }

        final String serviceConfigurationkey = restClient.value()
            .trim()
            .replace(StringConstants.TAB, StringConstants.SPACE)
            .replace(StringConstants.SPACE, StringConstants.DASH);

        return "voidframework.restclient.services." + serviceConfigurationkey;
    }
}
