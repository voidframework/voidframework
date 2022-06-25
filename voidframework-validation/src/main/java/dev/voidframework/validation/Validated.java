package dev.voidframework.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A validation result.
 *
 * @param <VALIDATED_OBJ_TYPE> Type of the validated object
 */
public class Validated<VALIDATED_OBJ_TYPE> {

    private final VALIDATED_OBJ_TYPE instance;
    private final Map<String, List<ValidationError>> validationErrorPerPathMap;

    /**
     * Build a new instance.
     *
     * @param instance                  The validated object instance
     * @param validationErrorPerPathMap The validation errors group by path
     */
    Validated(final VALIDATED_OBJ_TYPE instance,
              final Map<String, List<ValidationError>> validationErrorPerPathMap) {

        this.instance = instance;
        this.validationErrorPerPathMap = validationErrorPerPathMap != null ? validationErrorPerPathMap : new HashMap<>();
    }

    /**
     * Indicates if validated object has errors.
     *
     * @return {@code true} if validated object has one error or more, otherwise {@code false}
     */
    public boolean hasError() {

        return !this.validationErrorPerPathMap.isEmpty();
    }

    /**
     * Indicates if the specific path of validated object has errors.
     *
     * @param path The path
     * @return {@code true} if the specific path of validated object has one error or more, otherwise {@code false}
     */
    public boolean hasError(final String path) {

        final List<ValidationError> validationErrorList = this.validationErrorPerPathMap.get(path);
        return validationErrorList != null && !validationErrorList.isEmpty();
    }

    /**
     * Indicates if validated object is valid.
     *
     * @return {@code true} if validated object has no errors, otherwise {@code false}
     */
    public boolean isValid() {

        return this.validationErrorPerPathMap.isEmpty();
    }

    /**
     * Returns all errors of validated object.
     *
     * @return All errors group by path
     */
    public Map<String, List<ValidationError>> getError() {

        return this.validationErrorPerPathMap;
    }

    /**
     * Returns a single error for the specific path of validated object.
     *
     * @param path The path
     * @return The error if exists, otherwise, {@code null}
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
     */
    public List<ValidationError> getErrorList(final String path) {

        final List<ValidationError> validationErrorList = this.validationErrorPerPathMap.get(path);
        return validationErrorList == null ? Collections.emptyList() : validationErrorList;
    }

    /**
     * Returns instance of the validated object.
     *
     * @return The validated object
     */
    public VALIDATED_OBJ_TYPE getInstance() {

        return this.instance;
    }
}
