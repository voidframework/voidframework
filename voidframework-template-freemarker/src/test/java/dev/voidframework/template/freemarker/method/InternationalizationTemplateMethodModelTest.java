package dev.voidframework.template.freemarker.method;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.i18n.module.InternationalizationModule;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.template.freemarker.module.TemplateFreeMarkerModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Locale;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
final class InternationalizationTemplateMethodModelTest {

    private final Injector injector;

    public InternationalizationTemplateMethodModelTest() {

        final Config configuration = ConfigFactory.parseString("""
            voidframework.core.runInDevMode = false
            voidframework.template.basePackagePath = "/views/"
            """);

        this.injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                install(new InternationalizationModule());
                install(new TemplateFreeMarkerModule());
            }
        });
    }

    @Test
    void translationFound() {

        // Arrange
        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);

        // Act
        final String html = templateRenderer.render("internationalizationKeyFound.ftl", Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(html);
        Assertions.assertEquals("""
            Hello World!
            Hello World!""", html.trim());
    }

    @Test
    void translationNotFound() {

        // Arrange
        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);

        // Act
        final String html = templateRenderer.render("internationalizationKeyNotFound.ftl", Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(html);
        Assertions.assertEquals("""
            %unknown.key%
            %unknown.key%""", html.trim());
    }
}
