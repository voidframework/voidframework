package dev.voidframework.web.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Indicates that the elements necessary for the result to be processed by the template engine.
 */
public final class TemplateResult {

    /**
     * The name of the template to render.
     */
    public final String templateName;

    /**
     * The data model to use.
     */
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
     * @return Newly created instance
     */
    public static TemplateResult of(final String templateName) {
        return new TemplateResult(templateName, new HashMap<>());
    }

    /**
     * Build a new instance.
     *
     * @param templateName The name of the template to render
     * @param dataModel    The data model to use
     * @return Newly created instance
     */
    public static TemplateResult of(final String templateName,
                                    final Map<String, Object> dataModel) {
        return new TemplateResult(templateName, dataModel);
    }
}