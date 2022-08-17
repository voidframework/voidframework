package dev.voidframework.template.freemarker;

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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
final class FreeMarkerTemplateRendererTest {

    private final Injector injector;

    public FreeMarkerTemplateRendererTest() {

        final Config configuration = ConfigFactory.parseString("voidframework.core.runInDevMode = false");

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
    void dataModelNotProvided() {

        // Arrange
        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);

        // Act
        final TemplateException.DataModelNotProvided exception = Assertions.assertThrows(
            TemplateException.DataModelNotProvided.class,
            () -> templateRenderer.render("renderWithDataModel.ftl", Locale.ENGLISH, null));

        // Assert
        Assertions.assertEquals("Data model was not provided", exception.getMessage());
    }

    @Test
    void dataModelIsEmptyVariableNotFound() {

        // Arrange
        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);

        // Act
        final TemplateException.RenderingFailure exception = Assertions.assertThrows(
            TemplateException.RenderingFailure.class,
            () -> templateRenderer.render("renderWithDataModel.ftl", Locale.ENGLISH, new HashMap<>()));

        // Assert
        Assertions.assertEquals("Can't render template", exception.getMessage());
        Assertions.assertTrue(exception.getCause().getMessage().contains("The following has evaluated to null or missing"));
    }

    @Test
    void renderWithDataModelEnglish() {

        // Arrange
        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);

        final Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("price", 4500.99);

        // Act
        final String htmlEnglish = templateRenderer.render("renderWithDataModel.ftl", Locale.ENGLISH, dataModel);

        // Assert
        Assertions.assertNotNull(htmlEnglish);
        Assertions.assertEquals("This product costs 4,500.99 EUR.", htmlEnglish.trim());
    }

    @Test
    void renderWithDataModelFrench() {

        // Arrange
        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);

        final Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("price", 4500.99);

        // Act
        final String htmlFrench = templateRenderer.render("renderWithDataModel.ftl", Locale.FRENCH, dataModel);

        // Assert
        Assertions.assertNotNull(htmlFrench);
        Assertions.assertEquals("This product costs 4 500,99 EUR.", htmlFrench.trim());
    }

    @Test
    void renderWithoutDataModelEnglish() {

        // Arrange
        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);

        // Act
        final String htmlEnglish = templateRenderer.render("renderWithoutDataModel.ftl", Locale.ENGLISH);

        // Assert
        Assertions.assertNotNull(htmlEnglish);
        Assertions.assertEquals("This product costs 4,500.99 EUR.", htmlEnglish.trim());
    }

    @Test
    void renderWithoutDataModelFrench() {

        // Arrange
        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);

        // Act
        final String htmlFrench = templateRenderer.render("renderWithoutDataModel.ftl", Locale.FRENCH);

        // Assert
        Assertions.assertNotNull(htmlFrench);
        Assertions.assertEquals("This product costs 4 500,99 EUR.", htmlFrench.trim());
    }
}
