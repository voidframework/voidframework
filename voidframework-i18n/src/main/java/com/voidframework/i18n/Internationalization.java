package com.voidframework.i18n;

import java.util.Locale;

/**
 * Provides locale-specific messages.
 */
public interface Internationalization {

    /**
     * Retrieve a message.
     *
     * @param locale The locale corresponding to the translation to be used
     * @param key    The message key
     * @return The message, otherwise, a String containing the message key surrounded with "%"
     */
    String getMessage(final Locale locale, final String key);

    /**
     * Retrieve a message.
     *
     * @param locale        The locale corresponding to the translation to be used
     * @param key           The message key
     * @param argumentArray The message arguments
     * @return The message, otherwise, a String containing the message key surrounded with "%"
     */
    String getMessage(final Locale locale, final String key, final Object... argumentArray);

    /**
     * Retrieve a message.
     *
     * @param locale        The locale corresponding to the translation to be used
     * @param quantity      The quantity to determine the plural key to use
     * @param key           The message key
     * @param argumentArray The message arguments
     * @return The message, otherwise, a String containing the message key surrounded with "%"
     */
    String getMessage(final Locale locale, final long quantity, final String key, final Object... argumentArray);
}
