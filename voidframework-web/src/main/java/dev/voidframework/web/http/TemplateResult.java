package dev.voidframework.web.http;

import dev.voidframework.core.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Indicates that the elements necessary for the result to be processed by the template engine.
 *
 * @since 1.0.0
 */
public final class TemplateResult {

    /**
     * The name of the template to render.
     *
     * @since 1.0.0
     */
    public final String templateName;

    /**
     * The data model to use.
     *
     * @since 1.0.0
     */
    public final Map<String, Object> dataModel;

    /**
     * Build a new instance.
     *
     * @param templateName The name of the template to render
     * @param dataModel    The data model to use
     * @since 1.0.0
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
     * @since 1.0.0
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
     * @since 1.0.0
     */
    public static TemplateResult of(final String templateName,
                                    final Map<String, Object> dataModel) {

        return new TemplateResult(templateName, dataModel != null ? new HashMap<>(dataModel) : new HashMap<>());
    }

    /**
     * Build a new instance.
     *
     * @param templateName The name of the template to render
     * @param dataModel    The data model to use
     * @return Newly created instance
     * @since 1.0.0
     */
    public static TemplateResult of(final String templateName,
                                    final Object dataModel) {

        final Map<String, Object> map = JsonUtils.toMap(dataModel);
        return new TemplateResult(templateName, map != null ? map : new HashMap<>());
    }
}
