package dev.voidframework.sendmail;

import com.google.inject.Inject;
import dev.voidframework.core.lifecycle.LifeCycleStart;
import dev.voidframework.core.lifecycle.LifeCycleStop;
import dev.voidframework.core.utils.IOUtils;
import dev.voidframework.sendmail.engine.MailerEngine;
import dev.voidframework.sendmail.entity.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

/**
 * Mailer manager takes care of sending email asynchronously.
 *
 * @since 1.7.0
 */
public final class MailerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailerManager.class);

    private final long mailQueuePollTimeout;
    private final TransferQueue<Mail> mailToSendQueue;
    private MailerEngine mailerEngine;
    private Thread worker;
    private boolean isRunning;

    /**
     * Build a new instance.
     *
     * @param mailQueuePollTimeout Duration to wait when the queue is empty
     * @param mailToSendQueue      Instance of the mail transfer queue
     * @since 1.7.0
     */
    public MailerManager(final Duration mailQueuePollTimeout,
                         final TransferQueue<Mail> mailToSendQueue) {

        this.mailQueuePollTimeout = mailQueuePollTimeout.toMillis();
        this.mailToSendQueue = mailToSendQueue;
    }

    /**
     * Sets the mailer engine used to send emails.
     *
     * @param mailerEngine Mailer engine instance to use
     * @since 1.7.0
     */
    @Inject
    public void setMailerEngine(final MailerEngine mailerEngine) {

        this.mailerEngine = mailerEngine;
    }

    @LifeCycleStart(priority = 800)
    public void startSendmail() {

        this.isRunning = true;
        this.worker = new Thread(this::workerInnerLoop);
        this.worker.setName("Mailer");
        this.worker.start();
    }

    @LifeCycleStop(priority = 800, gracefulStopTimeoutConfigKey = "voidframework.sendmail.gracefulStopTimeout")
    public void stopSendmail() throws InterruptedException {

        this.isRunning = false;
        this.worker.join();
    }

    /**
     * Mailer thread inner loop.
     *
     * @since 1.7.0
     */
    private void workerInnerLoop() {

        while (this.isRunning) {

            try {
                Mail mailToSend;
                while ((mailToSend = this.mailToSendQueue.poll(mailQueuePollTimeout, TimeUnit.MILLISECONDS)) != null) {

                    if (!this.tryOpenConnection()) {
                        break;
                    }

                    this.trySend(mailToSend);
                }

                this.tryCloseConnection();
            } catch (final InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Tries to open mailer connection
     *
     * @return {@code true} if mail connection is open, otherwise {@code false}
     * @since 1.7.0
     */
    private boolean tryOpenConnection() {

        if (!this.mailerEngine.isConnected()) {
            try {
                this.mailerEngine.openConnection();
            } catch (final Exception exception) {
                LOGGER.error("Can't open mailer connection", exception);
                return false;
            }
        }

        return true;
    }

    /**
     * Tries to send mail.
     *
     * @param mailToSend Mail to send
     * @since 1.7.0
     */
    private void trySend(final Mail mailToSend) {

        try {
            this.mailerEngine.send(mailToSend);
            LOGGER.debug("Mail sent to {}", mailToSend.getRecipients());
        } catch (final Exception exception) {
            LOGGER.error("Can't send email", exception);
        } finally {
            mailToSend
                .getAttachmentList()
                .stream()
                .map(Mail.Attachment::inputStream)
                .forEach(IOUtils::closeWithoutException);
        }
    }

    /**
     * Tries to close mailer connection.
     *
     * @since 1.7.0
     */
    private void tryCloseConnection() {

        try {
            this.mailerEngine.closeConnection();
        } catch (final Exception exception) {
            LOGGER.error("Can't close mailer connection", exception);
        }
    }
}
