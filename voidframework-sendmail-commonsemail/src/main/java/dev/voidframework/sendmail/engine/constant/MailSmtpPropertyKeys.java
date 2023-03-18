package dev.voidframework.sendmail.engine.constant;

import java.util.List;

/**
 * Mail SMTP Property keys.
 *
 * @see <a href="https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html">JavaEE Mail SMTP Properties</a>
 * @since 1.7.0
 */
public final class MailSmtpPropertyKeys {

    /**
     * @since 1.7.0
     */
    public static final String USER = "mail.smtp.user";

    /**
     * @since 1.7.0
     */
    public static final String HOST = "mail.smtp.host";

    /**
     * @since 1.7.0
     */
    public static final String PORT = "mail.smtp.port";

    /**
     * @since 1.7.0
     */
    public static final String CONNECTIONTIMEOUT = "mail.smtp.connectiontimeout";

    /**
     * @since 1.7.0
     */
    public static final String TIMEOUT = "mail.smtp.timeout";

    /**
     * @since 1.7.0
     */
    public static final String WRITETIMEOUT = "mail.smtp.writetimeout";

    /**
     * @since 1.7.0
     */
    public static final String FROM = "mail.smtp.from";

    /**
     * @since 1.7.0
     */
    public static final String LOCALHOST = "mail.smtp.localhost";

    /**
     * @since 1.7.0
     */
    public static final String LOCALADDRESS = "mail.smtp.localaddress";

    /**
     * @since 1.7.0
     */
    public static final String LOCALPORT = "mail.smtp.localport";

    /**
     * @since 1.7.0
     */
    public static final String EHLO = "mail.smtp.ehlo";

    /**
     * @since 1.7.0
     */
    public static final String AUTH = "mail.smtp.auth";

    /**
     * @since 1.7.0
     */
    public static final String AUTH_MECHANISMS = "mail.smtp.auth.mechanisms";

    /**
     * @since 1.7.0
     */
    public static final String AUTH_LOGIN_DISABLE = "mail.smtp.auth.login.disable";

    /**
     * @since 1.7.0
     */
    public static final String AUTH_PLAIN_DISABLE = "mail.smtp.auth.plain.disable";

    /**
     * @since 1.7.0
     */
    public static final String AUTH_DIGEST_MD5_DISABLE = "mail.smtp.auth.digest-md5.disable";

    /**
     * @since 1.7.0
     */
    public static final String AUTH_NTLM_DISABLE = "mail.smtp.auth.ntlm.disable";

    /**
     * @since 1.7.0
     */
    public static final String AUTH_NTLM_DOMAIN = "mail.smtp.auth.ntlm.domain";

    /**
     * @since 1.7.0
     */
    public static final String AUTH_NTLM_FLAGS = "mail.smtp.auth.ntlm.flags";

    /**
     * @since 1.7.0
     */
    public static final String AUTH_XOAUTH2_DISABLE = "mail.smtp.auth.xoauth2.disable";

    /**
     * @since 1.7.0
     */
    public static final String SUBMITTER = "mail.smtp.submitter";

    /**
     * @since 1.7.0
     */
    public static final String DSN_NOTIFY = "mail.smtp.dsn.notify";

    /**
     * @since 1.7.0
     */
    public static final String DSN_RET = "mail.smtp.dsn.ret";

    /**
     * @since 1.7.0
     */
    public static final String ALLOW8BITMIME = "mail.smtp.allow8bitmime";

    /**
     * @since 1.7.0
     */
    public static final String SENDPARTIAL = "mail.smtp.sendpartial";

    /**
     * @since 1.7.0
     */
    public static final String SASL_ENABLE = "mail.smtp.sasl.enable";

    /**
     * @since 1.7.0
     */
    public static final String SASL_MECHANISMS = "mail.smtp.sasl.mechanisms";

    /**
     * @since 1.7.0
     */
    public static final String SASL_AUTHORIZATIONID = "mail.smtp.sasl.authorizationid";

    /**
     * @since 1.7.0
     */
    public static final String SASL_REALM = "mail.smtp.sasl.realm";

    /**
     * @since 1.7.0
     */
    public static final String SASL_USECANONICALHOSTNAME = "mail.smtp.sasl.usecanonicalhostname";

    /**
     * @since 1.7.0
     */
    public static final String QUITWAIT = "mail.smtp.quitwait";

    /**
     * @since 1.7.0
     */
    public static final String REPORTSUCCESS = "mail.smtp.reportsuccess";

    /**
     * @since 1.7.0
     */
    public static final String SOCKETFACTORY = "mail.smtp.socketFactory";

    /**
     * @since 1.7.0
     */
    public static final String SOCKETFACTORY_CLASS = "mail.smtp.socketFactory.class";

    /**
     * @since 1.7.0
     */
    public static final String SOCKETFACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";

    /**
     * @since 1.7.0
     */
    public static final String SOCKETFACTORY_PORT = "mail.smtp.socketFactory.port";

    /**
     * @since 1.7.0
     */
    public static final String SSL_ENABLE = "mail.smtp.ssl.enable";

    /**
     * @since 1.7.0
     */
    public static final String SSL_CHECKSERVERIDENTITY = "mail.smtp.ssl.checkserveridentity";

    /**
     * @since 1.7.0
     */
    public static final String SSL_TRUST = "mail.smtp.ssl.trust";

    /**
     * @since 1.7.0
     */
    public static final String SSL_SOCKETFACTORY = "mail.smtp.ssl.socketFactory";

    /**
     * @since 1.7.0
     */
    public static final String SSL_SOCKETFACTORY_CLASS = "mail.smtp.ssl.socketFactory.class";

    /**
     * @since 1.7.0
     */
    public static final String SSL_SOCKETFACTORY_PORT = "mail.smtp.ssl.socketFactory.port";

    /**
     * @since 1.7.0
     */
    public static final String SSL_PROTOCOLS = "mail.smtp.ssl.protocols";

    /**
     * @since 1.7.0
     */
    public static final String SSL_CIPHERSUITES = "mail.smtp.ssl.ciphersuites";

    /**
     * @since 1.7.0
     */
    public static final String STARTTLS_ENABLE = "mail.smtp.starttls.enable";

    /**
     * @since 1.7.0
     */
    public static final String STARTTLS_REQUIRED = "mail.smtp.starttls.required";

    /**
     * @since 1.7.0
     */
    public static final String PROXY_HOST = "mail.smtp.proxy.host";

    /**
     * @since 1.7.0
     */
    public static final String PROXY_PORT = "mail.smtp.proxy.port";

    /**
     * @since 1.7.0
     */
    public static final String PROXY_USER = "mail.smtp.proxy.user";

    /**
     * @since 1.7.0
     */
    public static final String PROXY_PASSWORD = "mail.smtp.proxy.password";

    /**
     * @since 1.7.0
     */
    public static final String SOCKS_HOST = "mail.smtp.socks.host";

    /**
     * @since 1.7.0
     */
    public static final String SOCKS_PORT = "mail.smtp.socks.port";

    /**
     * @since 1.7.0
     */
    public static final String MAILEXTENSION = "mail.smtp.mailextension";

    /**
     * @since 1.7.0
     */
    public static final String USERSET = "mail.smtp.userset";

    /**
     * @since 1.7.0
     */
    public static final String NOOP_STRICT = "mail.smtp.noop.strict";

    /**
     * Default constructor.
     *
     * @since 1.7.0
     */
    private MailSmtpPropertyKeys() {

        throw new UnsupportedOperationException("This is a class containing constants and cannot be instantiated");
    }

    /**
     * Returns all constant keys.
     *
     * @return An immutable list containing all keys
     * @since 1.7.0
     */
    public static List<String> keys() {

        return List.of(
            USER,
            HOST,
            PORT,
            CONNECTIONTIMEOUT,
            TIMEOUT,
            WRITETIMEOUT,
            FROM,
            LOCALHOST,
            LOCALADDRESS,
            LOCALPORT,
            EHLO,
            AUTH,
            AUTH_MECHANISMS,
            AUTH_LOGIN_DISABLE,
            AUTH_PLAIN_DISABLE,
            AUTH_DIGEST_MD5_DISABLE,
            AUTH_NTLM_DISABLE,
            AUTH_NTLM_DOMAIN,
            AUTH_NTLM_FLAGS,
            AUTH_XOAUTH2_DISABLE,
            SUBMITTER,
            DSN_NOTIFY,
            DSN_RET,
            ALLOW8BITMIME,
            SENDPARTIAL,
            SASL_ENABLE,
            SASL_MECHANISMS,
            SASL_AUTHORIZATIONID,
            SASL_REALM,
            SASL_USECANONICALHOSTNAME,
            QUITWAIT,
            REPORTSUCCESS,
            SOCKETFACTORY,
            SOCKETFACTORY_CLASS,
            SOCKETFACTORY_FALLBACK,
            SOCKETFACTORY_PORT,
            SSL_ENABLE,
            SSL_CHECKSERVERIDENTITY,
            SSL_TRUST,
            SSL_SOCKETFACTORY,
            SSL_SOCKETFACTORY_CLASS,
            SSL_SOCKETFACTORY_PORT,
            SSL_PROTOCOLS,
            SSL_CIPHERSUITES,
            STARTTLS_ENABLE,
            STARTTLS_REQUIRED,
            PROXY_HOST,
            PROXY_PORT,
            PROXY_USER,
            PROXY_PASSWORD,
            SOCKS_HOST,
            SOCKS_PORT,
            MAILEXTENSION,
            USERSET,
            NOOP_STRICT);
    }
}
