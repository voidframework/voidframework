package dev.voidframework.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A validation result.
 *
 * @param <T> Type of the validated object
 * @since 1.0.0
 */
public class Validated<T> {

    private final T instance;
    private final Map<String, List<ValidationError>> validationErrorPerPathMap;

    /**
     * Build a new instance.
     *
     * @param instance                  The validated object instance
     * @param validationErrorPerPathMap The validation errors group by path
     * @since 1.0.0
     */
    Validated(final T instance,
              final Map<String, List<ValidationError>> validationErrorPerPathMap) {

        this.instance = instance;
        this.validationErrorPerPathMap = validationErrorPerPathMap != null ? validationErrorPerPathMap : new HashMap<>();
    }

    /**
     * Creates an empty validated object.
     *
     * @param <T> Type of the validated object
     * @return Newly created instance
     * @since 1.0.0
     */
    public static <T> Validated<T> emptyOf() {

        return new Validated<>(null, new HashMap<>());
    }

    /**
     * Creates an empty validated object.
     *
     * @param instance The validated object instance
     * @param <T>      Type of the validated object
     * @return Newly created instance
     * @since 1.0.0
     */
    public static <T> Validated<T> emptyOf(final T instance) {

        return new Validated<>(instance, new HashMap<>());
    }

    /**
     * Indicates if validated object has errors.
     *
     * @return {@code true} if validated object has one error or more, otherwise {@code false}
     * @since 1.0.0
     */
    public boolean hasError() {

        return !this.validationErrorPerPathMap.isEmpty();
    }

    /**
     * Indicates if the specific path of validated object has errors.
     *
     * @param path The path
     * @return {@code true} if the specific path of validated object has one error or more, otherwise {@code false}
     * @since 1.0.0
     */
    public boolean hasError(final String path) {

        final List<ValidationError> validationErrorList = this.validationErrorPerPathMap.get(path);
        return validationErrorList != null && !validationErrorList.isEmpty();
    }

    /**
     * Indicates if validated object is valid.
     *
     * @return {@code true} if validated object has no errors, otherwise {@code false}
     * @since 1.0.0
     */
    public boolean isValid() {

        return this.validationErrorPerPathMap.isEmpty();
    }

    /**
     * Returns all errors of validated object.
     *
     * @return All errors group by path
     * @since 1.0.0
     */
    public Map<String, List<ValidationError>> getError() {

        return this.validationErrorPerPathMap;
    }

    /**
     * Returns a single error for the specific path of validated object.
     *
     * @param path The path
     * @return The error if exists, otherwise, {@code null}
     * @since 1.0.0
     */
    public ValidationError getError(final String path) {

        final List<ValidationError> validationErrorList = this.validationErrorPerPathMap.get(path);
        return validationErrorList == null ? null : validationErrorList.get(0);
    }

    /**
     * Returns all errors for the specific path of validated object.
     *
     * @param path The path
     * @return The error if exists, otherwise, {@code null}
     * @since 1.0.0
     */
    public List<ValidationError> getErrorList(final String path) {

        final List<ValidationError> validationErrorList = this.validationErrorPerPathMap.get(path);
        return validationErrorList == null ? Collections.emptyList() : validationErrorList;
    }

    /**
     * Returns instance of the validated object.
     *
     * @return The validated object
     * @since 1.0.0
     */
    public T getInstance() {

        return this.instance;
    }
}
