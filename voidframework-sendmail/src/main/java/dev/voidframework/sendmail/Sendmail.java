package dev.voidframework.sendmail;

import dev.voidframework.sendmail.entity.Mail;
import dev.voidframework.sendmail.exception.SendmailException;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TransferQueue;

/**
 * Sendmail takes care of sending mail asynchronously.
 *
 * @since 1.7.0
 */
public final class Sendmail {

    private final TransferQueue<Mail> mailToSendQueue;

    /**
     * Build a new instance.
     *
     * @param mailToSendQueue Instance of the mail transfer queue
     * @since 1.7.0
     */
    public Sendmail(final TransferQueue<Mail> mailToSendQueue) {

        this.mailToSendQueue = mailToSendQueue;
    }

    /**
     * Sends mail.
     *
     * @param mailToSend Mail to send
     */
    public void send(final Mail mailToSend) {

        if (mailToSend != null) {
            this.checkIsValidOrThrow(mailToSend);
            this.mailToSendQueue.add(mailToSend);
        }
    }

    /**
     * Checks if the mail to send is valid. If mail is
     * not valid, an exception will be thrown.
     *
     * @param mail Mail to be validated
     * @throws SendmailException.InvalidMail If mail is not valid
     */
    private void checkIsValidOrThrow(final Mail mail) throws SendmailException.InvalidMail {

        final boolean asAtLeastOneRecipient = !mail.getRecipients().isEmpty()
            || !mail.getCarbonCopyRecipients().isEmpty()
            || !mail.getBlindCarbonCopyRecipients().isEmpty();

        final boolean isValid = asAtLeastOneRecipient
            && mail.getCharset() != null
            && StringUtils.isNotBlank(mail.getFromAddress())
            && StringUtils.isNotBlank(mail.getReplyTo())
            && mail.getAttachmentList().stream().noneMatch(attachment -> attachment.inputStream() == null || StringUtils.isBlank(attachment.name()));

        if (!isValid) {
            throw new SendmailException.InvalidMail();
        }
    }
}
