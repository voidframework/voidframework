package service;

import com.google.inject.Inject;
import com.voidframework.core.bindable.Service;
import com.voidframework.i18n.ResourceBundleInternationalization;

import java.util.Locale;

@Service
public class HelloWorldService implements MonInterface {

    private final ResourceBundleInternationalization internationalization;

    @Inject
    public HelloWorldService(final ResourceBundleInternationalization internationalization) {
        this.internationalization = internationalization;
    }

    public String sayHello(final Locale locale) {
        return internationalization.getMessage(locale, "key");
    }
}
