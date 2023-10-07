package dev.voidframework.template.freemarker.method;

import dev.voidframework.i18n.Internationalization;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * FreeMarker method: internationalization (I18N).
 *
 * @since 1.0.0
 */
public class InternationalizationTemplateMethodModel implements TemplateMethodModelEx {

    private final Locale locale;
    private final Internationalization internationalization;

    /**
     * Build a new instance.
     *
     * @param locale               The locale to use
     * @param internationalization The internationalization instance
     * @since 1.0.0
     */
    public InternationalizationTemplateMethodModel(final Locale locale,
                                                   final Internationalization internationalization) {

        this.locale = locale;
        this.internationalization = internationalization;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public TemplateModel exec(final List argumentList) throws TemplateModelException {

        if (argumentList.isEmpty()) {
            throw new TemplateModelException("Wrong arguments");
        }

        final List<Object> parsedArgumentList = new ArrayList<>();
        for (final Object argument : argumentList) {
            if (argument instanceof SimpleScalar argumentAsSimpleScalar) {
                parsedArgumentList.add(argumentAsSimpleScalar.getAsString());
            } else if (argument instanceof SimpleNumber argumentAsSimpleNumber) {
                parsedArgumentList.add(argumentAsSimpleNumber.getAsNumber());
            } else {
                parsedArgumentList.add(argument);
            }
        }

        final String key = parsedArgumentList.get(0).toString();
        final String msg = this.internationalization.getMessage(
            locale,
            key,
            parsedArgumentList.stream().skip(1).toArray(Object[]::new));

        return new SimpleScalar(msg);
    }
}
