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
import dev.voidframework.restclient.exception.RestClientException;
import dev.voidframework.restclient.retrofit.CallAdapterFactory;
import dev.voidframework.restclient.retrofit.interceptor.ApiKeyAuthenticationInterceptor;
import dev.voidframework.restclient.retrofit.interceptor.BasicAuthenticationInterceptor;
import dev.voidframework.restclient.retrofit.interceptor.BearerAuthenticationInterceptor;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
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
                final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
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
                    .connectionPool(connectionPool);

                // Authentication interceptor
                final boolean needAuthentication = ConfigurationUtils.hasAnyPath(
                    this.configuration,
                    restClientConfigurationKeyPrefix + ".authentication.type",
                    "voidframework.restclient.authentication.type");
                if (needAuthentication) {
                    final AuthenticationType authType = ConfigurationUtils.getEnumOrFallback(
                        this.configuration,
                        restClientConfigurationKeyPrefix + ".authentication.type",
                        AuthenticationType.class,
                        "voidframework.restclient.authentication.type");

                    final Interceptor authInterceptor = switch (authType) {
                        case API_KEY -> createApiKeyAuthenticationInterceptor(restClientConfigurationKeyPrefix);
                        case BASIC -> createBasicAuthenticationInterceptor(restClientConfigurationKeyPrefix);
                        case BEARER -> createBearerAuthenticationInterceptor(restClientConfigurationKeyPrefix);
                    };

                    okHttpClientBuilder.addInterceptor(authInterceptor);
                }

                // Creates proxy instance
                final Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClientBuilder.build())
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
     * Create a new {@code ApiKeyAuthenticationInterceptor} instance.
     *
     * @param restClientConfigurationKeyPrefix The configuration key prefix
     * @return Newly created {@code ApiKeyAuthenticationInterceptor} instance
     * @see ApiKeyAuthenticationInterceptor
     * @since 1.10.0
     */
    private Interceptor createApiKeyAuthenticationInterceptor(final String restClientConfigurationKeyPrefix) {

        final String apiKeyName = ConfigurationUtils.getStringOrFallback(
            this.configuration,
            restClientConfigurationKeyPrefix + ".authentication.apiKeyName",
            "voidframework.restclient.authentication.apiKeyName");
        final String apiKeyValue = ConfigurationUtils.getStringOrFallback(
            this.configuration,
            restClientConfigurationKeyPrefix + ".authentication.apiKeyValue",
            "voidframework.restclient.authentication.apiKeyValue");
        final ApiKeyAuthenticationInterceptor.AddTo addTo = ConfigurationUtils.getEnumOrFallback(
            this.configuration,
            restClientConfigurationKeyPrefix + ".authentication.apiKeyAddTo",
            ApiKeyAuthenticationInterceptor.AddTo.class,
            "voidframework.restclient.authentication.apiKeyAddTo");

        return new ApiKeyAuthenticationInterceptor(apiKeyName, apiKeyValue, addTo);
    }

    /**
     * Create a new {@code BasicAuthenticationInterceptor} instance.
     *
     * @param restClientConfigurationKeyPrefix The configuration key prefix
     * @return Newly created {@code BasicAuthenticationInterceptor} instance
     * @see BasicAuthenticationInterceptor
     * @since 1.10.0
     */
    private Interceptor createBasicAuthenticationInterceptor(final String restClientConfigurationKeyPrefix) {

        final String basicUsername = ConfigurationUtils.getStringOrFallback(
            this.configuration,
            restClientConfigurationKeyPrefix + ".authentication.basicUsername",
            "voidframework.restclient.authentication.basicUsername");
        final String basicPassword = ConfigurationUtils.getStringOrFallback(
            this.configuration,
            restClientConfigurationKeyPrefix + ".authentication.basicPassword",
            "voidframework.restclient.authentication.basicPassword");
        final boolean useIso88591Encoding = ConfigurationUtils.getBooleanOrFallback(
            this.configuration,
            restClientConfigurationKeyPrefix + ".authentication.basicUseISO88591Encoding",
            "voidframework.restclient.authentication.basicUseISO88591Encoding");

        return new BasicAuthenticationInterceptor(basicUsername, basicPassword, useIso88591Encoding);
    }

    /**
     * Create a new {@code BearerAuthenticationInterceptor} instance.
     *
     * @param restClientConfigurationKeyPrefix The configuration key prefix
     * @return Newly created {@code BearerAuthenticationInterceptor} instance
     * @see BearerAuthenticationInterceptor
     * @since 1.10.0
     */
    private Interceptor createBearerAuthenticationInterceptor(final String restClientConfigurationKeyPrefix) {

        final String bearerPrefix = ConfigurationUtils.getStringOrFallback(
            this.configuration,
            restClientConfigurationKeyPrefix + ".authentication.bearerPrefix",
            "voidframework.restclient.authentication.bearerPrefix");
        final String bearerToken = ConfigurationUtils.getStringOrFallback(
            this.configuration,
            restClientConfigurationKeyPrefix + ".authentication.bearerToken",
            "voidframework.restclient.authentication.bearerToken");

        return new BearerAuthenticationInterceptor(bearerPrefix, bearerToken);
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
            throw new RestClientException.InvalidServiceIdentifier(restClient.value());
        }

        final String serviceConfigurationkey = restClient.value()
            .trim()
            .replace(StringConstants.TAB, StringConstants.SPACE)
            .replace(StringConstants.SPACE, StringConstants.DASH);

        return "voidframework.restclient.services." + serviceConfigurationkey;
    }
}
