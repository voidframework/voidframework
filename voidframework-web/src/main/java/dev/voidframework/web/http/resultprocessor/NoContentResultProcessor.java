package dev.voidframework.web.http.resultprocessor;

import com.typesafe.config.Config;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.web.http.Context;

import java.io.InputStream;

/**
 * No content ("do nothing") processor.
 *
 * @since 1.0.0
 */
public class NoContentResultProcessor implements ResultProcessor {

    @Override
    public void process(final Context context, final Config configuration, final TemplateRenderer templateRenderer) {

        // Nothing to do
    }

    @Override
    public InputStream getInputStream() {

        return null;
    }
}
