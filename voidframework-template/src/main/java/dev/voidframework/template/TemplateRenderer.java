package dev.voidframework.template;

import java.util.Locale;
import java.util.Map;

public interface TemplateRenderer {

    String render(final String templateName, final Locale locale);
    String render(final String templateName, final Locale locale, final Map<String, Object> dataModel);
}
