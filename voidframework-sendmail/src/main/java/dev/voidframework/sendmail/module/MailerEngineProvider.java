package dev.voidframework.sendmail.module;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.core.utils.ClassResolverUtils;
import dev.voidframework.sendmail.engine.MailerEngine;

/**
 * Mailer engine provider.
 *
 * @since 1.7.0
 */
@Singleton
public final class MailerEngineProvider implements Provider<MailerEngine> {

    private final Config configuration;
    private final Injector injector;
    private MailerEngine mailerEngine;

    /**
     * Build a new instance;
     *
     * @param configuration The application configuration
     * @param injector      The injector instance
     * @since 1.7.0
     */
    @Inject
    public MailerEngineProvider(final Config configuration, final Injector injector) {

        this.configuration = configuration;
        this.injector = injector;
    }

    @Override
    public MailerEngine get() {

        if (this.mailerEngine == null && configuration.hasPath("voidframework.sendmail.engine")) {
            final String sendmailEngineClassName = configuration.getString("voidframework.sendmail.engine");
            final Class<?> clazz = ClassResolverUtils.forName(sendmailEngineClassName);
            if (clazz != null) {
                final Injector childInjector = this.injector.createChildInjector(new AbstractModule() {

                    @Override
                    protected void configure() {

                        bind(clazz);
                    }
                });
                this.mailerEngine = (MailerEngine) childInjector.getInstance(clazz);
            }
        }

        return this.mailerEngine;
    }
}
