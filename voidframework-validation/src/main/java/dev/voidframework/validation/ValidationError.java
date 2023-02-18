package dev.voidframework.validation;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a single validation error.
 *
 * @param message       The translated error message
 * @param messageKey    The error message key
 * @param argumentArray The error message arguments
 * @since 1.0.0
 */
public record ValidationError(String message,
                              String messageKey,
                              Object... argumentArray) {

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ValidationError that = (ValidationError) o;
        return Objects.equals(message, that.message)
            && Objects.equals(messageKey, that.messageKey)
            && Arrays.equals(argumentArray, that.argumentArray);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(message, messageKey);
        result = 31 * result + Arrays.hashCode(argumentArray);
        return result;
    }

    @Override
    public String toString() {

        return "ValidationError{message='"
            + this.message
            + "', messageKey='" + this.messageKey
            + "', argumentArray=" + Arrays.toString(this.argumentArray)
            + '}';
    }
}
