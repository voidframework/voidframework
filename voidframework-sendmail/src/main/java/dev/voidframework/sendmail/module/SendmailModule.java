package dev.voidframework.sendmail.module;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import dev.voidframework.sendmail.MailerManager;
import dev.voidframework.sendmail.Sendmail;
import dev.voidframework.sendmail.engine.MailerEngine;
import dev.voidframework.sendmail.entity.Mail;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

/**
 * Sendmail module.
 *
 * @since 1.7.0
 */
public final class SendmailModule extends AbstractModule {

    private final Config configuration;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    public SendmailModule(final Config configuration) {

        this.configuration = configuration;
    }

    @Override
    protected void configure() {

        final TransferQueue<Mail> mailToSendQueue = new LinkedTransferQueue<>();
        final Sendmail sendmail = new Sendmail(mailToSendQueue);
        final MailerManager mailerManager = new MailerManager(
            this.configuration.getDuration("voidframework.sendmail.mailQueuePollTimeout"),
            mailToSendQueue);

        requestInjection(mailerManager);
        bind(MailerEngine.class).toProvider(MailerEngineProvider.class);
        bind(MailerManager.class).toInstance(mailerManager);
        bind(Sendmail.class).toInstance(sendmail);
    }
}
