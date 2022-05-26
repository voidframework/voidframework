package com.voidframework.template.freemarker.method;

import com.voidframework.i18n.Internationalization;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;
import java.util.Locale;

public class InternationalizationTemplateMethodModel implements TemplateMethodModelEx {

    private final Locale locale;
    private final Internationalization internationalization;

    public InternationalizationTemplateMethodModel(final Locale locale,
                                                   final Internationalization internationalization) {
        this.locale = locale;
        this.internationalization = internationalization;
    }

    @SuppressWarnings("unchecked")
    public TemplateModel exec(final List args) throws TemplateModelException {
        if (args.size() < 1) {
            throw new TemplateModelException("Wrong arguments");
        }

        final String key = ((SimpleScalar) args.get(0)).getAsString();
        final String msg = this.internationalization.getMessage(
            locale,
            key,
            args.stream().skip(1).map(SimpleScalar.class::cast).toArray(String[]::new));

        return new SimpleScalar(msg);
    }
}
