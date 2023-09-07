package dev.voidframework.restclient.retrofit.interceptor;

import dev.voidframework.core.constant.StringConstants;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Provides Bearer authentication (also called token authentication) mechanism.
 *
 * @since 1.10.0
 */
public final class BearerAuthenticationInterceptor implements Interceptor {

    private final String authorizationHeaderValue;

    /**
     * Build a new instance.
     *
     * @param bearerPrefix The bearer prefix
     * @param bearerToken  The bearer token
     * @since 1.10.0
     */
    public BearerAuthenticationInterceptor(final String bearerPrefix,
                                           final String bearerToken) {

        this.authorizationHeaderValue = bearerPrefix + StringConstants.SPACE + bearerToken;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {

        final Request request = chain.request();
        final Request authenticatedRequest = request.newBuilder()
            .header("Authorization", authorizationHeaderValue)
            .build();

        return chain.proceed(authenticatedRequest);
    }
}
