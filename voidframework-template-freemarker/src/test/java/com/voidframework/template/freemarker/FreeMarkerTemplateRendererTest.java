package com.voidframework.template.freemarker;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.voidframework.i18n.module.InternationalizationModule;
import com.voidframework.template.TemplateRenderer;
import com.voidframework.template.exception.TemplateRendererException;
import com.voidframework.template.freemarker.module.TemplateFreeMarkerModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class FreeMarkerTemplateRendererTest {

    private final Injector injector;

    public FreeMarkerTemplateRendererTest() {
        final Config configuration = ConfigFactory.parseString("""
            voidframework.core.runInDevMode=false
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
    public void dataModelNotProvided() {
        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);
        Assertions.assertNotNull(templateRenderer);

        final TemplateRendererException.DataModelNotProvided exception = Assertions.assertThrows(
            TemplateRendererException.DataModelNotProvided.class,
            () -> templateRenderer.render("renderWithDataModel.ftl", Locale.ENGLISH, null));
        Assertions.assertEquals("Data model was not provided", exception.getMessage());
    }

    @Test
    public void dataModelIsEmptyVariableNotFound() {
        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);
        Assertions.assertNotNull(templateRenderer);

        final TemplateRendererException.RenderingFailure exception = Assertions.assertThrows(
            TemplateRendererException.RenderingFailure.class,
            () -> templateRenderer.render("renderWithDataModel.ftl", Locale.ENGLISH, new HashMap<>()));
        Assertions.assertEquals("Can't render template", exception.getMessage());
        Assertions.assertTrue(exception.getCause().getMessage().contains("The following has evaluated to null or missing"));
    }

    @Test
    public void renderWithDataModel() {
        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);
        Assertions.assertNotNull(templateRenderer);

        final Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("price", 4500.99);

        final String htmlEnglish = templateRenderer.render("renderWithDataModel.ftl", Locale.ENGLISH, dataModel);
        Assertions.assertNotNull(htmlEnglish);
        Assertions.assertEquals("This product costs 4,500.99 EUR.", htmlEnglish.trim());

        final String htmlFrench = templateRenderer.render("renderWithDataModel.ftl", Locale.FRENCH, dataModel);
        Assertions.assertNotNull(htmlFrench);
        Assertions.assertEquals("This product costs 4 500,99 EUR.", htmlFrench.trim());
    }

    @Test
    public void renderWithoutDataModel() {
        final TemplateRenderer templateRenderer = this.injector.getInstance(TemplateRenderer.class);
        Assertions.assertNotNull(templateRenderer);

        final String htmlEnglish = templateRenderer.render("renderWithoutDataModel.ftl", Locale.ENGLISH);
        Assertions.assertNotNull(htmlEnglish);
        Assertions.assertEquals("This product costs 4,500.99 EUR.", htmlEnglish.trim());

        final String htmlFrench = templateRenderer.render("renderWithoutDataModel.ftl", Locale.FRENCH);
        Assertions.assertNotNull(htmlFrench);
        Assertions.assertEquals("This product costs 4 500,99 EUR.", htmlFrench.trim());
    }
}
