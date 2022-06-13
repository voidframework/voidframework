package dev.voidframework.validation;

/**
 * Represents a single validation error.
 */
public class ValidationError {

    private final String message;
    private final String messageKey;
    private final Object[] argumentArray;

    /**
     * Build a new instance.
     *
     * @param message       The translated error message
     * @param messageKey    The error message key
     * @param argumentArray The error message arguments
     */
    public ValidationError(final String message, final String messageKey, final Object... argumentArray) {
        this.message = message;
        this.messageKey = messageKey;
        this.argumentArray = argumentArray;
    }

    /**
     * Retrieves the translated error message.
     *
     * @return The translated error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Retrieves the error message key.
     *
     * @return The error message key
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Retrieves the error message arguments.
     *
     * @return The error message arguments
     */
    public Object[] getArgumentArray() {
        return argumentArray;
    }
}
