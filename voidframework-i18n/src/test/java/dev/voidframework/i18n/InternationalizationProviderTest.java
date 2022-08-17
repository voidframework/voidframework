package dev.voidframework.i18n;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.i18n.module.InternationalizationModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class InternationalizationProviderTest {

    @Test
    void injectorDoesNotExist() {

        // Arrange + Act
        final Config configuration = ConfigFactory.parseString("voidframework.i18n.engine =dev.voidframework.i18n.UnknownImplementationClass");
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                install(new InternationalizationModule());
            }
        });

        final Internationalization internationalization = injector.getInstance(Internationalization.class);

        // Assert
        Assertions.assertNotNull(internationalization);
        Assertions.assertTrue(internationalization instanceof ResourceBundleInternationalization);
    }

    @Test
    void injectorExist() {

        // Arrange + Act
        final Config configuration = ConfigFactory.parseString("voidframework.i18n.engine = dev.voidframework.i18n.ResourceBundleInternationalization");
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                install(new InternationalizationModule());
            }
        });

        final Internationalization internationalization = injector.getInstance(Internationalization.class);

        // Assert
        Assertions.assertNotNull(internationalization);
        Assertions.assertTrue(internationalization instanceof dev.voidframework.i18n.ResourceBundleInternationalization);
    }
}
