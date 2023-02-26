package dev.voidframework.template;

import java.util.Locale;
import java.util.Map;

/**
 * Template renderer must implements this interface.
 *
 * @since 1.0.0
 */
public interface TemplateRenderer {

    /**
     * Render a template.
     *
     * @param templateName The template name
     * @param locale       The locale to use for internationalization (Number format, Date, I18N, ...)
     * @return The rendered template
     * @since 1.0.0
     */
    String render(final String templateName, final Locale locale);

    /**
     * Render a template.
     *
     * @param templateName The template name
     * @param locale       The locale to use for internationalization (Number format, Date, I18N, ...)
     * @param dataModel    Data to use into the template
     * @return The rendered template
     * @since 1.0.0
     */
    String render(final String templateName, final Locale locale, final Map<String, Object> dataModel);
}
