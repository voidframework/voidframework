package dev.voidframework.template.freemarker;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.voidframework.i18n.Internationalization;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.template.exception.TemplateRendererException;
import dev.voidframework.template.freemarker.method.InternationalizationTemplateMethodModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * FreeMarker implementation of {@link TemplateRenderer}.
 */
@Singleton
public class FreeMarkerTemplateRenderer implements TemplateRenderer {

    private final Configuration freeMarkerConfiguration;
    private final Internationalization internationalization;

    /**
     * Build a new instance.
     *
     * @param freeMarkerConfiguration The FreeMarker configuration
     * @param internationalization    The internationalization instance
     */
    @Inject
    public FreeMarkerTemplateRenderer(final Configuration freeMarkerConfiguration,
                                      final Internationalization internationalization) {
        this.freeMarkerConfiguration = freeMarkerConfiguration;
        this.internationalization = internationalization;
    }

    @Override
    public String render(final String templateName, final Locale locale) {
        return render(templateName, locale, new HashMap<>());
    }

    @Override
    public String render(final String templateName, final Locale locale, final Map<String, Object> dataModel) {
        if (dataModel == null) {
            throw new TemplateRendererException.DataModelNotProvided();
        }

        final TemplateMethodModelEx internationalizationMethodModel = new InternationalizationTemplateMethodModel(
            locale,
            this.internationalization);
        dataModel.put("i18n", internationalizationMethodModel);
        dataModel.put("_", internationalizationMethodModel);

        dataModel.put("lang", locale.toLanguageTag());

        try {
            final Writer writer = new StringWriter();
            final Template template = this.freeMarkerConfiguration.getTemplate(templateName, locale);
            template.process(dataModel, writer);
            return writer.toString();
        } catch (final IOException | NullPointerException | TemplateException e) {
            throw new TemplateRendererException.RenderingFailure(e);
        }
    }
}
