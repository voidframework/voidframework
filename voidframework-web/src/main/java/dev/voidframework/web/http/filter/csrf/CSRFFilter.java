package dev.voidframework.web.http.filter.csrf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.lang.TypedMap;
import dev.voidframework.core.utils.HexUtils;
import dev.voidframework.web.exception.HttpException;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Cookie;
import dev.voidframework.web.http.FormItem;
import dev.voidframework.web.http.HttpContentType;
import dev.voidframework.web.http.HttpMethod;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.filter.Filter;
import dev.voidframework.web.http.filter.FilterChain;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * This filter takes care of generating and checking a CSRF
 * token if the request matches the chosen criteria.
 */
@Singleton
public class CSRFFilter implements Filter {

    public static final TypedMap.Key<String> CSRF_TOKEN_KEY = TypedMap.Key.of("CSRF_TOKEN", String.class);
    public static final TypedMap.Key<Boolean> BYPASS_CSRF_VERIFICATION = TypedMap.Key.of("BYPASS_CSRF_VERIFICATION", Boolean.class);

    private static final String H_MAC_ALGORITHM = "HmacSHA256";

    private final String csrfTokenName;
    private final String cookieName;
    private final boolean cookieSecure;
    private final boolean cookieHttpOnly;
    private final String signatureKey;
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
        this.signatureKey = configuration.getString("voidframework.web.csrf.signatureKey");
        this.timeToLive = configuration.getDuration("voidframework.web.csrf.timeToLive", TimeUnit.MILLISECONDS);

        if (StringUtils.isBlank(this.signatureKey)) {
            throw new ConfigException.BadValue("voidframework.web.csrf.signatureKey", "Please configure the CSRF signature Key");
        }
    }

    @Override
    public Result apply(final Context context, final FilterChain filterChain) {

        // Checks if CSRF token verification has to be performed
        if (context.getAttributes().get(BYPASS_CSRF_VERIFICATION) == Boolean.TRUE) {
            return filterChain.applyNext(context);
        }

        if (context.getRequest().getHttpMethod() == HttpMethod.POST) {
            final Pair<CSRFToken, String> currentAndNewCSRFTokenPair = extractAndRegenerateCSRFToken(context);
            final CSRFToken providedCSRFToken = extractProvidedCSRFToken(context);
            this.checkCSRFToken(currentAndNewCSRFTokenPair.getLeft(), providedCSRFToken);

            final Result result = filterChain.applyNext(context);
            if (result.getHttpCode() / 100 != 3) {
                // Result is not a redirection, we have to generate a new CSRF token
                return result.withCookie(Cookie.of(this.cookieName, currentAndNewCSRFTokenPair.getRight(), this.cookieHttpOnly, this.cookieSecure, null));
            }

            return result.withoutCookie(this.cookieName);
        } else if (context.getRequest().getHttpMethod() == HttpMethod.GET
            && context.getRequest().acceptContentType(HttpContentType.TEXT_HTML)) {

            final Pair<CSRFToken, String> tokens = extractAndRegenerateCSRFToken(context);

            return filterChain
                .applyNext(context)
                .withCookie(Cookie.of(this.cookieName, tokens.getRight(), this.cookieHttpOnly, this.cookieSecure, null));
        }

        return filterChain.applyNext(context);
    }

    /**
     * Extracts current CSRF token and creates a new one.
     * The newly created token will is automatically add to the context attributes.
     *
     * @param context The current context
     * @return A {@code Pair} containing the current {@code CSRFToken} and the new one as {@code String}
     */
    private Pair<CSRFToken, String> extractAndRegenerateCSRFToken(final Context context) {

        // Retrieves current CSRF token
        final CSRFToken currentCSRFToken = extractCurrentCSRFToken(context);

        // Create a new CSRF token
        final String newCSRFTokenAsString = this.createNewCSRFTokenAsString(currentCSRFToken);
        context.getAttributes().put(CSRF_TOKEN_KEY, newCSRFTokenAsString);

        return Pair.of(currentCSRFToken, newCSRFTokenAsString);
    }

    /**
     * Extracts the current CSRF token.
     *
     * @param context The current context
     * @return The current CSRF, otherwise, {@code null}
     */
    private CSRFToken extractCurrentCSRFToken(final Context context) {

        final Cookie csrfTokenCookie = context.getRequest().getCookie(this.cookieName);
        if (csrfTokenCookie == null) {
            return null;
        }

        final String[] csrfTokenPartArray = csrfTokenCookie.value().split(StringConstants.HYPHEN);
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
            final String[] csrfTokenPartArray = csrfTokenProvided.split(StringConstants.HYPHEN);
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
                    final String[] csrfTokenPartArray = csrfTokenProvided.split(StringConstants.HYPHEN);
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
            final String[] csrfTokenPartArray = csrfTokenProvided.split(StringConstants.HYPHEN);
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
            : UUID.randomUUID().toString().replace(StringConstants.HYPHEN, StringConstants.EMPTY);

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
            mac.init(new SecretKeySpec(this.signatureKey.getBytes(StandardCharsets.UTF_8), H_MAC_ALGORITHM));
            return HexUtils.toHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
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

        if (expectedValue == null || !Objects.equals(expectedValue, providedValue)) {
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

            return value + StringConstants.HYPHEN + nonce + StringConstants.HYPHEN + signature;
        }
    }
}
