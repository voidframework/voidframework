package dev.voidframework.web.http.resultprocessor;

import com.typesafe.config.Config;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.web.http.Context;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Process a simple object.
 */
public class ObjectResultProcessor implements ResultProcessor {

    private final Object object;

    /**
     * Build an empty new instance.
     * This constructor is useful during deserialize process
     */
    @SuppressWarnings("unused")
    public ObjectResultProcessor() {

        this.object = null;
    }

    /**
     * Build a new instance.
     *
     * @param object Object to process
     */
    public ObjectResultProcessor(final Object object) {

        this.object = object;
    }

    @Override
    public void process(final Context context, final Config configuration, final TemplateRenderer templateRenderer) {

        // Nothing to do
    }

    @Override
    public InputStream getInputStream() {

        if (object == null) {
            return InputStream.nullInputStream();
        } else if (object instanceof byte[]) {
            return new ByteArrayInputStream((byte[]) object);
        } else if (object instanceof InputStream) {
            return (InputStream) object;
        }

        return new ByteArrayInputStream(object.toString().getBytes(StandardCharsets.UTF_8));
    }
}
