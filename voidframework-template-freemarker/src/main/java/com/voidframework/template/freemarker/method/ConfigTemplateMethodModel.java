package com.voidframework.template.freemarker.method;

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

public class ConfigTemplateMethodModel implements TemplateMethodModelEx {

    private final Config configuration;

    public ConfigTemplateMethodModel(final Config configuration) {
        this.configuration = configuration;
    }

    public TemplateModel exec(final List args) throws TemplateModelException {
        if (args.size() != 1) {
            throw new TemplateModelException("Wrong arguments");
        }

        final Object value = this.configuration.getAnyRef(((SimpleScalar) args.get(0)).getAsString());
        if (value instanceof String) {
            return new SimpleScalar((String) value);
        } else if (value instanceof Number) {
            return new SimpleNumber((Number) value);
        } else if (value instanceof Boolean) {
            return (Boolean) value ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
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
                        return (Boolean) obj ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
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
