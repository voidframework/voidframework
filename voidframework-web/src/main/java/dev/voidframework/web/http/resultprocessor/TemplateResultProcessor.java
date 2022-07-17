package dev.voidframework.web.http.resultprocessor;

import com.typesafe.config.Config;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.template.exception.TemplateException;
import dev.voidframework.web.csrf.CSRFFilter;
import dev.voidframework.web.http.Context;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Process a template.
 */
public class TemplateResultProcessor implements ResultProcessor {

    private final String templateName;
    private final Map<String, Object> dataModel;

    private InputStream inputStream;

    /**
     * Build an empty new instance.
     * This constructor is useful during deserialize process
     */
    @SuppressWarnings("unused")
    public TemplateResultProcessor() {

        this.templateName = null;
        this.dataModel = null;
        this.inputStream = null;
    }

    /**
     * Build a new instance.
     *
     * @param templateName The name of the template to render
     * @param dataModel    The data model to use
     */
    public TemplateResultProcessor(final String templateName, final Map<String, Object> dataModel) {

        this.templateName = templateName;
        this.dataModel = dataModel;
        this.inputStream = null;
    }

    @Override
    public void process(final Context context, final Config configuration, final TemplateRenderer templateRenderer) {

        if (templateRenderer == null) {
            throw new TemplateException.NoTemplateEngine();
        }

        if (this.dataModel != null) {
            this.dataModel.put("flash", context.getFlashMessages());
            this.dataModel.put("session", context.getSession());
            this.dataModel.put("languages", configuration.getStringList("voidframework.web.language.availableLanguages"));
            this.dataModel.put("csrfToken", context.getAttributes().get(CSRFFilter.CSRF_TOKEN_KEY));
        }

        final String renderedTemplate = templateRenderer.render(this.templateName, context.getLocale(), this.dataModel);
        this.inputStream = new ByteArrayInputStream(renderedTemplate.getBytes(StandardCharsets.UTF_8));

        context.getFlashMessages().clear();
    }

    @Override
    public InputStream getInputStream() {

        return this.inputStream;
    }
}
