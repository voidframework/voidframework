package dev.voidframework.validation;

import com.google.inject.Singleton;
import dev.voidframework.core.bindable.Service;
import jakarta.validation.Configuration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This service provides methods for validating an object (JSR 380).
 */
@Service
@Singleton
public final class ValidationService {

    // Validators do not return the arguments, but simply the value that was tested.
    // This list indicates arguments to be retrieved from the attributes.
    private static final List<String> SORTED_ARGUMENT_TO_EXTRACT_LIST = List.of(
        "value", "min", "max", "integer", "years", "months", "days", "hours", "minutes", "seconds", "millis", "nanos", "inclusive");

    private final Map<Locale, Validator> validatorPerLocaleMap;

    /**
     * Build a new instance.
     */
    public ValidationService() {
        this.validatorPerLocaleMap = new ConcurrentHashMap<>();
    }

    /**
     * Validates an object.
     *
     * @param objectToValidate       The object to validate
     * @param <OBJ_TO_VALIDATE_TYPE> Type of the object to validate
     * @return The validated object
     */
    public <OBJ_TO_VALIDATE_TYPE> Validated<OBJ_TO_VALIDATE_TYPE> validate(final OBJ_TO_VALIDATE_TYPE objectToValidate) {
        return this.validate(objectToValidate, Locale.getDefault());
    }

    /**
     * Validates an object.
     *
     * @param objectToValidate       The object to validate
     * @param locale                 The locale to use for validation error message
     * @param <OBJ_TO_VALIDATE_TYPE> Type of the object to validate
     * @return The validated object
     */
    public <OBJ_TO_VALIDATE_TYPE> Validated<OBJ_TO_VALIDATE_TYPE> validate(final OBJ_TO_VALIDATE_TYPE objectToValidate,
                                                                           final Locale locale) {

        final Map<String, List<ValidationError>> validationErrorPerKeyMap = new HashMap<>();

        // Object to validate is null, abord!
        if (objectToValidate == null) {
            return new Validated<>(null, validationErrorPerKeyMap);
        }

        // Retrieves the correct validator according to the locale and validates the object provided
        final Validator validator = this.validatorPerLocaleMap.computeIfAbsent(locale, this::createValidator);
        final Set<ConstraintViolation<OBJ_TO_VALIDATE_TYPE>> constraintViolationSet = validator.validate(objectToValidate);
        for (final ConstraintViolation<OBJ_TO_VALIDATE_TYPE> constraintViolation : constraintViolationSet) {
            final String fieldKey = constraintViolation.getPropertyPath().toString();
            final List<ValidationError> validationErrorList = validationErrorPerKeyMap.computeIfAbsent(
                fieldKey,
                (key) -> new ArrayList<>());

            validationErrorList.add(
                new ValidationError(
                    constraintViolation.getMessage(),
                    constraintViolation.getMessageTemplate().replaceAll("[{}]", ""),
                    createMessageArgumentArray(constraintViolation)));
        }

        // Result
        return new Validated<>(objectToValidate, validationErrorPerKeyMap);
    }

    /**
     * Creates message arguments array
     *
     * @param constraintViolation The constraint violation
     * @return The message arguments array
     */
    private Object[] createMessageArgumentArray(final ConstraintViolation<?> constraintViolation) {
        final Map<String, Object> attributeMap = constraintViolation.getConstraintDescriptor().getAttributes();
        return SORTED_ARGUMENT_TO_EXTRACT_LIST.stream()
            .filter(attributeMap::containsKey)
            .map(attributeMap::get)
            .toArray();
    }

    /**
     * Creates a new validator.
     *
     * @param locale The locale to use with the message interpolator
     * @return The newly created validator
     */
    private Validator createValidator(final Locale locale) {
        Configuration<?> configuration = Validation.byDefaultProvider().configure();
        configuration = configuration.messageInterpolator(new MessageInterpolatorWithLocale(configuration.getDefaultMessageInterpolator(), locale));

        try (final ValidatorFactory validatorFactory = configuration.buildValidatorFactory()) {
            return validatorFactory.getValidator();
        }
    }

    /**
     * Message interpolator with a custom locale.
     *
     * @param delegate The base message interpolator
     * @param locale   The locale
     */
    private record MessageInterpolatorWithLocale(MessageInterpolator delegate, Locale locale) implements MessageInterpolator {

        @Override
        public String interpolate(final String message, final Context context) {
            return this.delegate.interpolate(message, context, locale);
        }

        @Override
        public String interpolate(final String message, final Context context, final Locale locale) {
            return this.delegate.interpolate(message, context, locale);
        }
    }
}
