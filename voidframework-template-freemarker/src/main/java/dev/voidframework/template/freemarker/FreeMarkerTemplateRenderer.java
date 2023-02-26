package dev.voidframework.template.freemarker;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import dev.voidframework.i18n.Internationalization;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.template.exception.TemplateException;
import dev.voidframework.template.freemarker.method.InternationalizationTemplateMethodModel;
import dev.voidframework.template.freemarker.method.ReverseRouteTemplateMethodModel;
import dev.voidframework.web.http.routing.Router;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateMethodModelEx;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * FreeMarker implementation of {@link TemplateRenderer}.
 *
 * @since 1.0.0
 */
@Singleton
public class FreeMarkerTemplateRenderer implements TemplateRenderer {

    private final Configuration freeMarkerConfiguration;
    private final Internationalization internationalization;
    private TemplateMethodModelEx reverseRouteTemplateMethodModel;

    /**
     * Build a new instance.
     *
     * @param injector The injector instance
     * @since 1.0.0
     */
    @Inject
    public FreeMarkerTemplateRenderer(final Injector injector) {

        this.freeMarkerConfiguration = injector.getInstance(Configuration.class);
        this.internationalization = injector.getInstance(Internationalization.class);

        try {
            this.reverseRouteTemplateMethodModel = new ReverseRouteTemplateMethodModel(injector.getInstance(Router.class));
        } catch (final Throwable ignore) { // NOSONAR
            this.reverseRouteTemplateMethodModel = null;
        }
    }

    @Override
    public String render(final String templateName, final Locale locale) {

        return render(templateName, locale, new HashMap<>());
    }

    @Override
    public String render(final String templateName, final Locale locale, final Map<String, Object> dataModel) {

        if (dataModel == null) {
            throw new TemplateException.DataModelNotProvided();
        }

        final TemplateMethodModelEx internationalizationMethodModel = new InternationalizationTemplateMethodModel(
            locale,
            this.internationalization);

        dataModel.put("i18n", internationalizationMethodModel);
        dataModel.put("_", internationalizationMethodModel);
        dataModel.put("lang", locale.toLanguageTag());
        dataModel.put("urlfor", reverseRouteTemplateMethodModel);

        try {
            final Writer writer = new StringWriter();
            final Template template = this.freeMarkerConfiguration.getTemplate(templateName, locale);
            template.process(dataModel, writer);
            return writer.toString();
        } catch (final freemarker.template.TemplateException exception) {
            throw new TemplateException.RenderingFailure(templateName, exception.getEndLineNumber() - 1, exception);
        } catch (final ParseException exception) {
            throw new TemplateException.RenderingFailure(templateName, exception.getEndLineNumber() - 1, exception);
        } catch (final Exception exception) {
            throw new TemplateException.RenderingFailure(templateName, -1, exception);
        }
    }
}
