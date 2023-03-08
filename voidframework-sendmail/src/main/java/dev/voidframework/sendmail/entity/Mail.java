package dev.voidframework.sendmail.entity;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A single mail.
 *
 * @since 1.7.0
 */
public final class Mail {

    private final Set<String> recipientSet;
    private final Set<String> carbonCopyRecipientSet;
    private final Set<String> blindCarbonCopyRecipientSet;
    private final List<Attachment> attachmentList;
    private Charset charset;
    private String subject;
    private String bodyContentHtml;
    private String bodyContentText;
    private String fromAddress;
    private String fromName;
    private String replyTo;

    /**
     * Build a new instance.
     *
     * @since 1.7.0
     */
    public Mail() {

        this.charset = StandardCharsets.UTF_8;
        this.recipientSet = new HashSet<>();
        this.carbonCopyRecipientSet = new HashSet<>();
        this.blindCarbonCopyRecipientSet = new HashSet<>();
        this.attachmentList = new ArrayList<>();
    }

    /**
     * Gets the defined charset.
     *
     * @return The charset defined in this mail
     * @since 1.7.0
     */
    public Charset getCharset() {

        return this.charset;
    }

    /**
     * Sets the mail charset.
     *
     * @param charset The charset defined in this mail
     * @since 1.7.0
     */
    public void setCharset(final Charset charset) {

        this.charset = charset;
    }

    /**
     * Gets the subject.
     *
     * @return The subject of this mail
     * @since 1.7.0
     */
    public String getSubject() {

        return this.subject;
    }

    /**
     * Sets the subject.
     *
     * @param subject The subject to set
     * @since 1.7.0
     */
    public void setSubject(final String subject) {

        this.subject = subject;
    }

    /**
     * Gets the HTML body content.
     *
     * @return The HTML body content
     * @since 1.7.0
     */
    public String getBodyContentHtml() {

        return this.bodyContentHtml;
    }

    /**
     * Sets the HTML body content.
     *
     * @param bodyContentHtml The HTML body content
     * @since 1.7.0
     */
    public void setBodyContentHtml(final String bodyContentHtml) {

        this.bodyContentHtml = bodyContentHtml;
    }

    /**
     * Gets the TEXT body content.
     *
     * @return The TEXT body content
     * @since 1.7.0
     */
    public String getBodyContentText() {

        return this.bodyContentText;
    }

    /**
     * Sets the TEXT body content.
     *
     * @param bodyContentText The TEXT body content
     * @since 1.7.0
     */
    public void setBodyContentText(final String bodyContentText) {

        this.bodyContentText = bodyContentText;
    }

    /**
     * Gets the sender address.
     *
     * @return The sender address
     * @since 1.7.0
     */
    public String getFromAddress() {

        return this.fromAddress;
    }

    /**
     * Gets the sender name.
     *
     * @return The sender name
     * @since 1.7.0
     */
    public String getFromName() {

        return this.fromName;
    }

    /**
     * Sets the sender (aka. From).
     *
     * @param address The sender address
     * @since 1.7.0
     */
    public void setFrom(final String address) {

        this.fromAddress = address;
        this.fromName = null;
    }

    /**
     * Sets the sender (aka. From).
     *
     * @param address The sender address
     * @param name    The sender name
     * @since 1.7.0
     */
    public void setFrom(final String address, final String name) {

        this.fromAddress = address;
        this.fromName = name;
    }

    /**
     * Gets the destination to use in case recipient choose to reply to your mail.
     *
     * @return The destination to use as "reply-to"
     * @since 1.7.0
     */
    public String getReplyTo() {

        return this.replyTo == null ? this.fromAddress : this.replyTo;
    }

    /**
     * Sets the destination to use in case recipient choose to reply to your mail.
     *
     * @param replyTo The destination to use as "reply-to"
     * @since 1.7.0
     */
    public void setReplyTo(final String replyTo) {

        this.replyTo = replyTo;
    }

    /**
     * Gets all recipients.
     *
     * @return A set of recipients
     * @since 1.7.0
     */
    public Set<String> getRecipients() {

        return this.recipientSet;
    }

    /**
     * Adds recipient.
     *
     * @param recipient Recipient to add
     * @since 1.7.0
     */
    public void addRecipient(final String recipient) {

        this.recipientSet.add(recipient);
    }

    /**
     * Gets all carbon copy recipients.
     *
     * @return A set of carbon copy recipients
     * @since 1.7.0
     */
    public Set<String> getCarbonCopyRecipients() {

        return this.carbonCopyRecipientSet;
    }

    /**
     * Adds carbon copy recipient.
     *
     * @param carbonCopyRecipient Carbon copy recipient to add
     * @since 1.7.0
     */
    public void addCarbonCopyRecipient(final String carbonCopyRecipient) {

        this.carbonCopyRecipientSet.add(carbonCopyRecipient);
    }

    /**
     * Gets all blind carbon copy recipients.
     *
     * @return A set of blind carbon copy recipients
     * @since 1.7.0
     */
    public Set<String> getBlindCarbonCopyRecipients() {

        return this.blindCarbonCopyRecipientSet;
    }

    /**
     * Adds blind carbon copy recipient.
     *
     * @param blindCarbonCopyRecipient Blind carbon copy recipient to add
     * @since 1.7.0
     */
    public void addBlindCarbonCopyRecipient(final String blindCarbonCopyRecipient) {

        this.blindCarbonCopyRecipientSet.add(blindCarbonCopyRecipient);
    }

    /**
     * Gets all attachments.
     *
     * @return A list of attachments
     * @since 1.7.0
     */
    public List<Attachment> getAttachmentList() {

        return this.attachmentList;
    }

    /**
     * Adds an attachment.
     *
     * @param name        Name of the attachment (aka filename)
     * @param inputStream Attachment content
     * @since 1.7.0
     */
    public void addAttachment(final String name, final InputStream inputStream) {

        this.attachmentList.add(new Attachment(name, inputStream));
    }

    /**
     * A single mail attachment.
     *
     * @param name        Name of the attachment (aka filename)
     * @param inputStream Attachment content
     * @since 1.7.0
     */
    public record Attachment(String name,
                             InputStream inputStream) {
    }
}
