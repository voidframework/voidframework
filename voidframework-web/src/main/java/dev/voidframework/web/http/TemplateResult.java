package dev.voidframework.web.http;

import java.util.HashMap;
import java.util.Map;

public final class TemplateResult {

    public final String templateName;
    public final Map<String, Object> dataModel;

    /**
     * Build a new instance.
     *
     * @param templateName The name of the template to render
     * @param dataModel    The data model to use
     */
    private TemplateResult(final String templateName,
                           final Map<String, Object> dataModel) {
        this.templateName = templateName;
        this.dataModel = dataModel;
    }

    /**
     * Build a new instance.
     *
     * @param templateName The name of the template to render
     */
    public static TemplateResult of(final String templateName) {
        return new TemplateResult(templateName, new HashMap<>());
    }

    /**
     * Build a new instance.
     *
     * @param templateName The name of the template to render
     * @param dataModel    The data model to use
     */
    public static TemplateResult of(final String templateName,
                                    final Map<String, Object> dataModel) {
        return new TemplateResult(templateName, dataModel);
    }
}
