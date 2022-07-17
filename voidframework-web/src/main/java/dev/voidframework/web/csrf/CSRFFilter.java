package dev.voidframework.web.csrf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.core.helper.Hex;
import dev.voidframework.core.lang.TypedMap;
import dev.voidframework.web.exception.HttpException;
import dev.voidframework.web.filter.Filter;
import dev.voidframework.web.filter.FilterChain;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Cookie;
import dev.voidframework.web.http.FormItem;
import dev.voidframework.web.http.HttpContentType;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.routing.HttpMethod;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * This filter takes care of generating and checking a CSRF
 * token if the request matches the chosen criteria.
 */
@Singleton
public class CSRFFilter implements Filter {

    public static final TypedMap.Key<String> CSRF_TOKEN_KEY = TypedMap.Key.of("CSRF_TOKEN");
    public static final TypedMap.Key<Boolean> BYPASS_CSRF_VERIFICATION = TypedMap.Key.of("BYPASS_CSRF_VERIFICATION");

    private static final String H_MAC_ALGORITHM = "HmacSHA1";

    private final String csrfTokenName;
    private final String cookieName;
    private final boolean cookieSecure;
    private final boolean cookieHttpOnly;
    private final String cryptoKey;
    private final long timeToLive;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    @Inject
    public CSRFFilter(final Config configuration) {

        this.csrfTokenName = configuration.getString("voidframework.web.csrf.tokenName");
        this.cookieName = configuration.getString("voidframework.web.csrf.cookieName");
        this.cookieSecure = configuration.getBoolean("voidframework.web.csrf.cookieSecure");
        this.cookieHttpOnly = configuration.getBoolean("voidframework.web.csrf.cookieHttpOnly");
        this.cryptoKey = configuration.getString("voidframework.web.csrf.cryptoKey");
        this.timeToLive = configuration.getLong("voidframework.web.csrf.timeToLive");
    }

    @Override
    public Result apply(final Context context, final FilterChain filterChain) {

        // Checks if CSRF token verification has to be performed
        if (context.getAttributes().get(BYPASS_CSRF_VERIFICATION) == Boolean.TRUE) {
            return filterChain.applyNext(context);
        }

        final CSRFToken csrfTokenExpected = extractExpectedCSRFTokenHash(context);
        if (context.getRequest().getHttpMethod() == HttpMethod.POST) {
            final CSRFToken csrfTokenProvided = extractProvidedCSRFToken(context);
            this.checkCSRFToken(csrfTokenExpected, csrfTokenProvided);

            return filterChain.applyNext(context).withoutCookie(this.cookieName);
        } else if (context.getRequest().getHttpMethod() == HttpMethod.GET
            && context.getRequest().acceptContentType(HttpContentType.TEXT_HTML)) {

            final String csrfToken = this.createNewCSRFTokenAsString(csrfTokenExpected);
            context.getAttributes().put(CSRF_TOKEN_KEY, csrfToken);

            return filterChain
                .applyNext(context)
                .withCookie(Cookie.of(this.cookieName, csrfToken, this.cookieHttpOnly, this.cookieSecure, null));
        }

        return filterChain.applyNext(context);
    }

    /**
     * Extracts the expected CSRF token.
     *
     * @param context The current context
     * @return The provided CSRF, otherwise, {@code null}
     */
    private CSRFToken extractExpectedCSRFTokenHash(final Context context) {

        final Cookie csrfTokenCookie = context.getRequest().getCookie(this.cookieName);
        if (csrfTokenCookie == null) {
            return null;
        }

        final String[] csrfTokenPartArray = csrfTokenCookie.value().split("-");
        if (csrfTokenPartArray.length != 3) {
            return null;
        }

        return new CSRFToken(csrfTokenPartArray[0], Long.parseLong(csrfTokenPartArray[1]), csrfTokenPartArray[2]);
    }

    /**
     * Extracts the provided CSRF token.
     *
     * @param context The current context
     * @return The provided CSRF, otherwise, {@code null}
     */
    private CSRFToken extractProvidedCSRFToken(final Context context) {

        // Query String
        String csrfTokenProvided = context.getRequest().getQueryStringParameter(this.csrfTokenName);
        if (StringUtils.isNotBlank(csrfTokenProvided)) {
            final String[] csrfTokenPartArray = csrfTokenProvided.split("-");
            if (csrfTokenPartArray.length != 3) {
                return null;
            }

            return new CSRFToken(csrfTokenPartArray[0], Long.parseLong(csrfTokenPartArray[1]), csrfTokenPartArray[2]);
        }

        // Content body
        final Map<String, List<FormItem>> formData = context.getRequest().getBodyContent().asFormData();
        if (formData != null) {
            final List<FormItem> csrfFormItem = formData.getOrDefault(this.csrfTokenName, List.of());
            if (!csrfFormItem.isEmpty()) {
                csrfTokenProvided = csrfFormItem.get(0).value();

                if (StringUtils.isNotBlank(csrfTokenProvided)) {
                    final String[] csrfTokenPartArray = csrfTokenProvided.split("-");
                    if (csrfTokenPartArray.length != 3) {
                        return null;
                    }

                    return new CSRFToken(csrfTokenPartArray[0], Long.parseLong(csrfTokenPartArray[1]), csrfTokenPartArray[2]);
                }
            }
        }

        // Header
        csrfTokenProvided = context.getRequest().getHeader("X-CSRF-TOKEN");
        if (StringUtils.isNotBlank(csrfTokenProvided)) {
            final String[] csrfTokenPartArray = csrfTokenProvided.split("-");
            if (csrfTokenPartArray.length != 3) {
                return null;
            }

            return new CSRFToken(csrfTokenPartArray[0], Long.parseLong(csrfTokenPartArray[1]), csrfTokenPartArray[2]);
        }

        return null;
    }

    /**
     * Creates a new CSRF token.
     *
     * @param existingCSRFToken An existing CSRF token (could be null)
     * @return The newly creates CSRF token as String
     */
    private String createNewCSRFTokenAsString(final CSRFToken existingCSRFToken) {

        final long currentTimeMillis = System.currentTimeMillis();
        final String value = existingCSRFToken != null
            ? existingCSRFToken.value
            : UUID.randomUUID().toString().replace("-", "");

        return new CSRFToken(
            value,
            currentTimeMillis,
            generateSignature(value + currentTimeMillis)).toString();
    }

    /**
     * Generates a signature for the given value.
     *
     * @param value The value to sign
     * @return The signature
     */
    private String generateSignature(final String value) {

        try {
            final Mac mac = Mac.getInstance(H_MAC_ALGORITHM);
            mac.init(new SecretKeySpec(this.cryptoKey.getBytes(StandardCharsets.UTF_8), H_MAC_ALGORITHM));
            return Hex.toHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (final InvalidKeyException | NoSuchAlgorithmException exception) {
            throw new HttpException.InternalServerError(exception);
        }
    }

    /**
     * Checks the CSRF token.
     *
     * @param csrfTokenExpected The expected CSRF token
     * @param csrfTokenProvided The provided CSRF token
     */
    private void checkCSRFToken(final CSRFToken csrfTokenExpected, final CSRFToken csrfTokenProvided) {

        if (csrfTokenExpected == null || csrfTokenProvided == null) {
            throw new HttpException.BadRequest("CSRF token not found");
        }

        if ((csrfTokenProvided.nonce + this.timeToLive) < System.currentTimeMillis()) {
            throw new HttpException.BadRequest("CSRF token is expired");
        }

        final String expectedValue = Objects.equals(csrfTokenExpected.signature, generateSignature(csrfTokenExpected.value + csrfTokenExpected.nonce))
            ? csrfTokenExpected.value
            : null;
        final String providedValue = Objects.equals(csrfTokenProvided.signature, generateSignature(csrfTokenProvided.value + csrfTokenProvided.nonce))
            ? csrfTokenProvided.value
            : null;

        if (!Objects.equals(expectedValue, providedValue)) {
            throw new HttpException.BadRequest("CSRF token is invalid");
        }
    }

    /**
     * A CSRF token.
     *
     * @param value     The token value
     * @param nonce     When the token has been created (in milliseconds)
     * @param signature The token signature
     */
    private record CSRFToken(String value,
                             long nonce,
                             String signature) {

        @Override
        public String toString() {

            return value + "-" + nonce + "-" + signature;
        }
    }
}