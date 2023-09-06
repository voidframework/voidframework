package dev.voidframework.restclient.retrofit.interceptor;

import dev.voidframework.core.constant.StringConstants;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Provides API key authentication mechanism.
 *
 * @since 1.10.0
 */
public final class ApiKeyAuthenticationInterceptor implements Interceptor {

    private final String key;
    private final String value;
    private final AddTo addTo;

    /**
     * Build a new instance.
     *
     * @param key   The API key variable name
     * @param value The API key value
     * @param addTo Where to add the API key (ie: HEADERS)
     * @since 1.10.0
     */
    public ApiKeyAuthenticationInterceptor(final String key,
                                           final String value,
                                           final AddTo addTo) {

        this.key = key;
        this.value = value;
        this.addTo = addTo;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {

        final Request request = chain.request();
        final Request authenticatedRequest = switch (this.addTo) {
            case COOKIE -> request.newBuilder()
                .header("Cookie", this.key + StringConstants.EQUAL + this.value)
                .build();
            case HEADER -> request.newBuilder()
                .header(this.key, this.value)
                .build();
            case QUERY_PARAMETER -> {
                final HttpUrl url = request.url()
                    .newBuilder()
                    .addQueryParameter(this.key, this.value)
                    .build();
                yield request.newBuilder().url(url).build();
            }
        };

        return chain.proceed(authenticatedRequest);
    }

    /**
     * Available locations where to add API key.
     *
     * @since 1.10.0
     */
    public enum AddTo {

        /**
         * API authentication will be added to request cookie.
         *
         * @since 1.10.0
         */
        COOKIE,

        /**
         * API authentication will be added to request headers.
         *
         * @since 1.10.0
         */
        HEADER,

        /**
         * API authentication will be added to request query parameters.
         *
         * @since 1.10.0
         */
        QUERY_PARAMETER
    }
}
