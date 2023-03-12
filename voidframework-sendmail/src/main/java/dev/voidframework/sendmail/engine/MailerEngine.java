package dev.voidframework.sendmail.engine;

import dev.voidframework.sendmail.entity.Mail;

/**
 * Sends mails.
 *
 * @since 1.7.0
 */
public interface MailerEngine {

    /**
     * Checks if this mailer currently connected.
     *
     * @return {@code true} if this mailer is currently connected, otherwise {@code false}
     * @since 1.7.0
     */
    boolean isConnected();

    /**
     * Opens connection.
     *
     * @throws Exception If an error occur while opening the connection
     * @since 1.7.0
     */
    void openConnection() throws Exception; // NOSONAR

    /**
     * Closes connection.
     *
     * @throws Exception If something wrong occur while closing the connection
     * @since 1.7.0
     */
    void closeConnection() throws Exception; // NOSONAR

    /**
     * Sends a single mail.
     *
     * @param mail Mail to send
     * @throws Exception If something wrong occur while sending email
     * @since 1.7.0
     */
    void send(final Mail mail) throws Exception; // NOSONAR
}
