package dev.voidframework.template.freemarker.module;

import com.google.inject.AbstractModule;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.template.freemarker.FreeMarkerTemplateRenderer;
import freemarker.template.Configuration;

/**
 * FreeMarker template module.
 *
 * @since 1.0.0
 */
public class TemplateFreeMarkerModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(Configuration.class).toProvider(FreeMarkerConfigurationProvider.class);
        bind(TemplateRenderer.class).to(FreeMarkerTemplateRenderer.class);
    }
}
