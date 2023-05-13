package dev.voidframework.sendmail.engine;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.sendmail.engine.constant.MailMimePropertyKeys;
import dev.voidframework.sendmail.engine.constant.MailSmtpPropertyKeys;
import dev.voidframework.sendmail.entity.Mail;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Implementation of sendmail based on Apache Commons Email library.
 *
 * @since 1.7.0
 */
@Singleton
public class ApacheCommonsEmailMailerEngine implements MailerEngine {

    private static final String TRANSPORT_PROTOCOL = "smtp";
    private static final String CONFIGURATION_PREFIX = "voidframework.sendmail.commonsemail.";

    private final Session session;
    private final Transport transport;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @since 1.7.0
     */
    @Inject
    public ApacheCommonsEmailMailerEngine(final Config configuration) throws NoSuchProviderException {

        // Configures system properties
        configureSystemProperties(configuration);

        // Creates session & transport
        final Properties sessionProperties = createSessionProperties(configuration);

        final String username = configuration.hasPath("voidframework.sendmail.commonsemail.username")
            ? configuration.getString("voidframework.sendmail.commonsemail.username")
            : sessionProperties.getProperty(MailSmtpPropertyKeys.USER);
        final String password = configuration.hasPath("voidframework.sendmail.commonsemail.password")
            ? configuration.getString("voidframework.sendmail.commonsemail.password")
            : null;

        if (StringUtils.isBlank(username)) {
            sessionProperties.setProperty(MailSmtpPropertyKeys.AUTH, "false");
            this.session = Session.getInstance(sessionProperties);
        } else {
            sessionProperties.setProperty(MailSmtpPropertyKeys.AUTH, "true");
            this.session = Session.getInstance(sessionProperties, new DefaultAuthenticator(username, password));
        }

        this.transport = session.getTransport(TRANSPORT_PROTOCOL);
    }

    /**
     * Configures system properties.
     *
     * @param configuration The application configuration
     * @since 1.7.0
     */
    private static void configureSystemProperties(final Config configuration) {

        for (final String applicableSystemProperty : MailMimePropertyKeys.keys()) {

            final String confKey = CONFIGURATION_PREFIX + applicableSystemProperty;
            if (configuration.hasPath(confKey)) {
                final String confValue = configuration.getString(confKey);
                System.setProperty(applicableSystemProperty, confValue);
            }
        }
    }

    /**
     * Creates the session properties.
     *
     * @param configuration The application configuration
     * @return Session properties
     * @since 1.7.0
     */
    private static Properties createSessionProperties(final Config configuration) {

        final Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", TRANSPORT_PROTOCOL);

        for (final String applicableProperty : MailSmtpPropertyKeys.keys()) {

            final String confKey = CONFIGURATION_PREFIX + applicableProperty;
            if (configuration.hasPath(confKey)) {
                final String confValue = configuration.getString(confKey);
                properties.setProperty(applicableProperty, confValue);
            }
        }

        return properties;
    }

    @Override
    public boolean isConnected() {

        return this.transport.isConnected();
    }

    @Override
    public void openConnection() throws Exception {

        this.transport.connect();
    }

    @Override
    public void closeConnection() throws Exception {

        this.transport.close();
    }

    @Override
    public void send(final Mail mail) throws Exception {

        final HtmlEmail htmlEmail = new HtmlEmail();
        htmlEmail.setCharset(mail.getCharset().name());
        htmlEmail.setSubject(mail.getSubject());
        htmlEmail.setFrom(mail.getFromAddress(), mail.getFromName(), mail.getCharset().name());
        htmlEmail.setBoolHasAttachments(!mail.getAttachmentList().isEmpty());

        if (mail.getBodyContentText() != null) {
            htmlEmail.setTextMsg(mail.getBodyContentText());
        }
        if (mail.getBodyContentHtml() != null) {
            htmlEmail.setHtmlMsg(mail.getBodyContentHtml());
        }
        if (mail.getReplyTo() != null) {
            htmlEmail.addReplyTo(mail.getReplyTo());
        } else {
            htmlEmail.addReplyTo(mail.getFromAddress());
        }
        for (final String recipient : mail.getRecipients()) {
            htmlEmail.addTo(recipient);
        }
        for (final String carbonCopyRecipient : mail.getCarbonCopyRecipients()) {
            htmlEmail.addCc(carbonCopyRecipient);
        }
        for (final String blindCarbonCopyRecipient : mail.getBlindCarbonCopyRecipients()) {
            htmlEmail.addBcc(blindCarbonCopyRecipient);
        }

        htmlEmail.setMailSession(session);
        htmlEmail.buildMimeMessage();

        final MimeMessage mimeMessage = htmlEmail.getMimeMessage();
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
    }
}
