package dev.voidframework.sendmail.engine;

import dev.voidframework.core.bindable.Bindable;
import dev.voidframework.sendmail.entity.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy implementation. This implementation simply displays sent mail to the console.
 *
 * @since 1.7.0
 */
@Bindable
public final class DummyMailerEngine implements MailerEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyMailerEngine.class);

    private static final String FALLBACK_FIELD_CONTENT = "<empty>";
    private static final String FALLBACK_HTML_BODY = "<html body is empty>";
    private static final String FALLBACK_TEXT_BODY = "<text body is empty>";

    @Override
    public boolean isConnected() {

        return true;
    }

    @Override
    public void openConnection() {

        // Nothing to do
    }

    @Override
    public void closeConnection() {

        // Nothing to do
    }

    @Override
    public void send(final Mail mail) {

        String fromAddress;
        if (mail.getFromAddress() == null) {
            fromAddress = FALLBACK_FIELD_CONTENT;
        } else {
            fromAddress = mail.getFromAddress() + (mail.getFromName() == null ? "" : "<" + mail.getFromName() + ">");
        }

        LOGGER.info("""
            New mail request!
            ------------------------------------------------------------------------------
            Charset ........... %s
            Subject ........... %s
            From .............. %s
            Reply-To .......... %s
            To ................ %s
            CC ................ %s
            BCC ............... %s
            Attachment ........ %d
            ------------------------------------------------------------------------------
            %s
            ------------------------------------------------------------------------------
            %s
            ------------------------------------------------------------------------------
            """
            .stripIndent()
            .stripTrailing()
            .formatted(
                mail.getCharset() == null ? FALLBACK_FIELD_CONTENT : mail.getCharset(),
                mail.getSubject() == null ? FALLBACK_FIELD_CONTENT : mail.getSubject(),
                fromAddress,
                mail.getReplyTo() == null ? FALLBACK_FIELD_CONTENT : mail.getReplyTo(),
                mail.getRecipients().isEmpty() ? FALLBACK_FIELD_CONTENT : String.join(", ", mail.getRecipients()),
                mail.getCarbonCopyRecipients().isEmpty() ? FALLBACK_FIELD_CONTENT : String.join(", ", mail.getCarbonCopyRecipients()),
                mail.getBlindCarbonCopyRecipients().isEmpty() ? FALLBACK_FIELD_CONTENT : String.join(", ", mail.getBlindCarbonCopyRecipients()),
                mail.getAttachmentList().size(),
                mail.getBodyContentText() == null ? FALLBACK_TEXT_BODY : mail.getBodyContentText(),
                mail.getBodyContentHtml() == null ? FALLBACK_HTML_BODY : mail.getBodyContentHtml()));
    }
}
