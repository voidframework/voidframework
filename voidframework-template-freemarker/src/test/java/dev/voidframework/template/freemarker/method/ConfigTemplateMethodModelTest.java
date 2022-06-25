package dev.voidframework.template.freemarker.method;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.i18n.module.InternationalizationModule;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.template.exception.TemplateException;
import dev.voidframework.template.freemarker.module.TemplateFreeMarkerModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Locale;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ConfigTemplateMethodModelTest {

    private final Injector injector;

    public ConfigTemplateMethodModelTest() {

        final Config configuration = ConfigFactory.parseString("""
            voidframework.core.runInDevMode = true
            test.number = 42
            test.string = "Hello World!"
            test.object = {a:1337}
            test.listString = ["en", "fr"]
            test.listNumber = [1, 2, 3]
            test.listBoolean = [true, false]
            test.listObject = [{a:42}, {a:1337}]
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
    public void configurationFound() {

        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);
        Assertions.assertNotNull(templateRenderer);

        final String html = templateRenderer.render("retrieveConfigurationFromTemplate.ftl", Locale.ENGLISH);
        Assertions.assertNotNull(html);
        Assertions.assertEquals("""
            Devel Model = TRUE

            42
            Hello World!
            Hello World!
            1337
            {a=1337}

            [en][fr]
            [1][2][3]
            [Y][N]
            [{a=42}][{a=1337}]""", html.trim());
    }

    @Test
    public void configuratioNotFound() {

        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);
        Assertions.assertNotNull(templateRenderer);

        final TemplateException.RenderingFailure exception = Assertions.assertThrows(
            TemplateException.RenderingFailure.class,
            () -> templateRenderer.render("configurationNotFound.ftl", Locale.ENGLISH));
        Assertions.assertEquals("Can't render template", exception.getMessage());
        Assertions.assertEquals("String: 1: No configuration setting found for key 'unknownKey'", exception.getCause().getCause().getMessage());
    }

    @Test
    public void wrongArguments() {

        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);
        Assertions.assertNotNull(templateRenderer);

        final TemplateException.RenderingFailure exception = Assertions.assertThrows(
            TemplateException.RenderingFailure.class,
            () -> templateRenderer.render("wrongArguments.ftl", Locale.ENGLISH));
        Assertions.assertEquals("Can't render template", exception.getMessage());
        Assertions.assertTrue(exception.getCause().getMessage().contains("Wrong arguments"));
    }
}
