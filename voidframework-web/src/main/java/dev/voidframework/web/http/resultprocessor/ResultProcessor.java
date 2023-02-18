package dev.voidframework.web.http.resultprocessor;

import com.typesafe.config.Config;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.web.http.Context;

import java.io.InputStream;

/**
 * Result processor. In charge to transform a content (any type) into an {@code InputStream}.
 *
 * @since 1.0.0
 */
public interface ResultProcessor {

    /**
     * Process the result.
     *
     * @param context          The current context
     * @param configuration    The application configuration
     * @param templateRenderer The template rendered if available
     * @since 1.0.0
     */
    void process(final Context context, final Config configuration, final TemplateRenderer templateRenderer);

    /**
     * Get the result input stream.
     *
     * @return The result input stream
     * @since 1.0.0
     */
    InputStream getInputStream();
}
