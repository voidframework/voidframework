package dev.voidframework.core.remoteconfiguration;

import java.util.Locale;

/**
 * Represents a simple key / value object. Key must be a simple
 * string and value could be a string representing anything (ie:
 * [1,2,3]).
 */
public final class KeyValueCfgObject {

    private final String key;
    private Object value;

    /**
     * Build a new instance.
     *
     * @param key   The configuration key
     * @param value The configuration value
     */
    public KeyValueCfgObject(final String key, final String value) {

        this.key = key.trim();
        final String cleanedValue = value.trim();

        if (!cleanedValue.startsWith("\"")
            && !cleanedValue.startsWith("[")
            && !cleanedValue.startsWith("{")
            && cleanedValue.compareToIgnoreCase("null") != 0) {

            // Check if value is a boolean
            if (cleanedValue.compareToIgnoreCase("true") == 0
                || cleanedValue.compareToIgnoreCase("false") == 0) {
                this.value = Boolean.parseBoolean(cleanedValue);
            } else {
                try {

                    // Check if value is a number
                    this.value = Long.parseLong(cleanedValue);
                } catch (final NumberFormatException ignore) {
                    try {
                        this.value = Double.parseDouble(cleanedValue);
                    } catch (final NumberFormatException ignore2) {

                        // Fallback to a quoted value
                        this.value = "\"" + cleanedValue + "\"";
                    }
                }
            }
        } else {
            this.value = cleanedValue;
        }
    }

    /**
     * Tries to add this configuration object into
     * the application configuration.
     *
     * @param appConfig The application configuration content
     */
    public void apply(final StringBuilder appConfig) {

        appConfig
            .append(this.key)
            .append(" = ")
            .append(this.value == null ? "null" : this.value)
            .append("\n");
    }

    @Override
    public String toString() {

        return KeyValueCfgObject.class.getSimpleName()
            + "["
            + this.key
            + " <- "
            + this.value
            + "]";
    }

    /**
     * Same as {@link #toString()} but with a potential mask applied to the value.
     *
     * @return A string representation of the object
     */
    public String toStringWithAdaptativeMask() {

        final boolean maskValueOnToString = isSensitiveValue();
        return KeyValueCfgObject.class.getSimpleName()
            + "["
            + this.key
            + " <- "
            + (maskValueOnToString ? "**********" : this.value)
            + "]";
    }

    /**
     * Checks if the current object hold a sensitive value.
     *
     * @return {@code true} if the current object hold a sensitive value, otherwise, {@code false}
     */
    private boolean isSensitiveValue() {

        final String keyLowerCase = this.key.toLowerCase(Locale.ENGLISH);
        return keyLowerCase.contains("pass") || keyLowerCase.contains("secret") || keyLowerCase.contains("token");
    }
}
