package com.voidframework.template.freemarker.module;

import com.google.inject.AbstractModule;
import com.voidframework.template.TemplateRenderer;
import com.voidframework.template.freemarker.FreeMarkerTemplateRenderer;
import freemarker.template.Configuration;

public class TemplateFreeMarkerModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(Configuration.class).toProvider(FreeMarkerConfigurationProvider.class);
        bind(TemplateRenderer.class).to(FreeMarkerTemplateRenderer.class);
    }
}
