package dev.voidframework.restclient.retrofit.interceptor;

import dev.voidframework.core.constant.StringConstants;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Provides Basic authentication mechanism.
 *
 * @since 1.10.0
 */
public final class BasicAuthenticationInterceptor implements Interceptor {

    private final String authorizationHeaderValue;

    /**
     * Build a new instance.
     *
     * @param username            The username
     * @param password            The password
     * @param useIso88591Encoding Use ISO-8859-1 encoding rather than UTF-8
     * @since 1.10.0
     */
    public BasicAuthenticationInterceptor(final String username,
                                          final String password,
                                          final boolean useIso88591Encoding) {

        final Charset charset = useIso88591Encoding ? StandardCharsets.ISO_8859_1 : StandardCharsets.UTF_8;
        final byte[] authData = (username + StringConstants.COLON + password).getBytes(charset);
        final String b64Token = Base64.getEncoder().encodeToString(authData);

        this.authorizationHeaderValue = "Basic" + StringConstants.SPACE + b64Token;
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
