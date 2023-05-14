package dev.voidframework.template.freemarker.method;

import com.typesafe.config.Config;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

import java.util.Collection;
import java.util.List;

/**
 * FreeMarker method: retrieves value from the configuration into the template.
 *
 * @since 1.0.0
 */
public class ConfigTemplateMethodModel implements TemplateMethodModelEx {

    private final Config configuration;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @since 1.0.0
     */
    public ConfigTemplateMethodModel(final Config configuration) {

        this.configuration = configuration;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public TemplateModel exec(final List argumentList) throws TemplateModelException {

        if (argumentList.size() != 1) {
            throw new TemplateModelException("Wrong arguments");
        }

        final String configPath = ((SimpleScalar) argumentList.get(0)).getAsString();
        if (!this.configuration.hasPath(configPath)) {
            return null;
        }

        final Object value = this.configuration.getAnyRef(configPath);
        if (value instanceof String) {
            return new SimpleScalar((String) value);
        } else if (value instanceof Number) {
            return new SimpleNumber((Number) value);
        } else if (value instanceof Boolean) {
            return value == Boolean.TRUE ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        } else if (value instanceof Collection<?>) {
            return new TemplateSequenceModel() {

                private final List<?> valueList = ((Collection<?>) value).stream().toList();

                @Override
                public TemplateModel get(final int index) {

                    final Object obj = valueList.get(index);

                    if (obj instanceof String) {
                        return new SimpleScalar((String) obj);
                    } else if (obj instanceof Number) {
                        return new SimpleNumber((Number) obj);
                    } else if (obj instanceof Boolean) {
                        return obj == Boolean.TRUE ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
                    } else {
                        return obj == null ? null : new SimpleScalar(obj.toString());
                    }
                }

                @Override
                public int size() {

                    return valueList.size();
                }
            };
        } else {
            return value == null ? null : new SimpleScalar(value.toString());
        }
    }
}
