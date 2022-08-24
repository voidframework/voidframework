package dev.voidframework.web.server.http;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import dev.voidframework.web.http.Session;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Signs and verifies the session.
 */
public final class SessionSigner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionSigner.class);
    private static final List<String> SESSION_INTERNAL_KEY_LIST = Arrays.asList("exp", "iat", "nbf");

    private final SessionSignerConfiguration sessionSignerConfiguration;
    private final JWTVerifier verifier;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    public SessionSigner(final Config configuration) {

        this.sessionSignerConfiguration = new SessionSignerConfiguration(
            configuration.getString("voidframework.web.session.signatureKey"),
            configuration.getDuration("voidframework.web.session.timeToLive"));

        if (this.sessionSignerConfiguration.signatureKey.equalsIgnoreCase("changeme")
            || StringUtils.isBlank(this.sessionSignerConfiguration.signatureKey)) {
            throw new ConfigException.BadValue("voidframework.web.session.signatureKey", "Please configure the Session signature Key");
        }

        final JWTVerifier.BaseVerification verification = (JWTVerifier.BaseVerification)
            JWT.require(Algorithm.HMAC256(this.sessionSignerConfiguration.signatureKey))
                .acceptLeeway(5)
                .acceptExpiresAt(5);
        this.verifier = verification.build();
    }

    /**
     * Verifies the signed session.
     *
     * @param signedSession The signed session to verify
     * @return The session retrieved from the signed session, otherwise, an empty session
     */
    public Session verify(final String signedSession) {

        try {
            final DecodedJWT decodedJWT = verifier.verify(signedSession);

            final Map<String, String> sessionContent = new HashMap<>();
            for (final Map.Entry<String, Claim> claim : decodedJWT.getClaims().entrySet()) {
                if (!SESSION_INTERNAL_KEY_LIST.contains(claim.getKey())) {
                    sessionContent.put(claim.getKey(), claim.getValue().asString());
                }
            }

            return new Session(sessionContent);
        } catch (final JWTVerificationException ignore) {
            // This exception is not important, it simply means that the session has expired
        }

        return new Session();
    }

    /**
     * Signs the session.
     *
     * @param session The session to sign
     * @return The signed session
     */
    public String sign(final Session session) {

        try {
            final Instant instantUtc = Instant.now();
            final Date dateNowUtc = Date.from(instantUtc);
            final Date dateExpirationUtc = Date.from(instantUtc.plus(this.sessionSignerConfiguration.timeToLive));

            return JWT.create()
                .withIssuedAt(dateNowUtc)
                .withNotBefore(dateNowUtc)
                .withExpiresAt(dateExpirationUtc)
                .withPayload(session)
                .sign(Algorithm.HMAC256(this.sessionSignerConfiguration.signatureKey));
        } catch (final IllegalArgumentException | JWTCreationException exception) {
            LOGGER.error("Can't sign session", exception);
            return null;
        }
    }

    /**
     * Session signer configuration.
     *
     * @param signatureKey The key to be used to sign the session data
     * @param timeToLive   The maximum lifetime allowed for a session
     */
    private record SessionSignerConfiguration(String signatureKey,
                                              Duration timeToLive) {
    }
}
