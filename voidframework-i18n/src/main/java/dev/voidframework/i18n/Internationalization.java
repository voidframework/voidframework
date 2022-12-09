package dev.voidframework.i18n;

import java.util.Locale;
import java.util.Map;

/**
 * Provides locale-specific messages.
 */
public interface Internationalization {

    /**
     * Retrieves a message.
     *
     * @param locale The locale corresponding to the translation to be used
     * @param key    The message key
     * @return The message, otherwise, a String containing the message key surrounded with "%"
     */
    String getMessage(final Locale locale, final String key);

    /**
     * Retrieves a message.
     *
     * @param locale        The locale corresponding to the translation to be used
     * @param key           The message key
     * @param argumentArray The message arguments
     * @return The message, otherwise, a String containing the message key surrounded with "%"
     */
    String getMessage(final Locale locale, final String key, final Object... argumentArray);

    /**
     * Retrieves a message.
     *
     * @param locale        The locale corresponding to the translation to be used
     * @param quantity      The quantity to determine the plural key to use
     * @param key           The message key
     * @param argumentArray The message arguments
     * @return The message, otherwise, a String containing the message key surrounded with "%"
     */
    String getMessage(final Locale locale, final long quantity, final String key, final Object... argumentArray);

    /**
     * Retrieves all messages with linked key.
     *
     * @param locale The locale
     * @return A {@code Map} containing all messages with linked key
     */
    Map<String, String> getAllMessages(final Locale locale);
}
