package dev.voidframework.i18n;

import dev.voidframework.core.bindable.BindClass;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Resource bundle based internationalization.
 */
@BindClass
public final class ResourceBundleInternationalization implements Internationalization {

    private static final String BUNDLE_BASE_NAME = "messages";
    private static final String UNKNOWN_KEY_SURROUNDING = "%";

    private final Map<Locale, ResourceBundle> bundlePerLocaleCacheMap;

    /**
     * Build a new instance.
     */
    public ResourceBundleInternationalization() {

        this.bundlePerLocaleCacheMap = new HashMap<>();
    }

    @Override
    public String getMessage(final Locale locale, final String key) {

        if (locale == null) {
            return UNKNOWN_KEY_SURROUNDING + key + UNKNOWN_KEY_SURROUNDING;
        }

        ResourceBundle resourceBundle = this.bundlePerLocaleCacheMap.get(locale);
        if (resourceBundle == null) {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale, this.getClass().getClassLoader());
            this.bundlePerLocaleCacheMap.put(locale, resourceBundle);
        }

        try {
            return resourceBundle.getString(key);
        } catch (final MissingResourceException ignore) {
            return UNKNOWN_KEY_SURROUNDING + key + UNKNOWN_KEY_SURROUNDING;
        }
    }

    @Override
    public String getMessage(final Locale locale, final String key, final Object... argumentArray) {

        return MessageFormat.format(this.getMessage(locale, key), argumentArray);
    }

    @Override
    public String getMessage(final Locale locale, final long quantity, final String key, final Object... argumentArray) {

        final String resolvedMessageFormat;
        if (quantity == 0) {
            resolvedMessageFormat = this.getMessage(locale, key + ".0");
        } else if (quantity == 1 || quantity == -1) {
            resolvedMessageFormat = this.getMessage(locale, key + ".1");
        } else {
            resolvedMessageFormat = this.getMessage(locale, key + ".2");
        }

        return MessageFormat.format(resolvedMessageFormat, argumentArray);
    }
}
