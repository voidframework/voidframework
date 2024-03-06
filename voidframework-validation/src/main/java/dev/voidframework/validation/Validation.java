package dev.voidframework.validation;

import com.google.inject.Singleton;
import dev.voidframework.core.bindable.Service;
import dev.voidframework.core.constant.StringConstants;
import jakarta.validation.Configuration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This service provides methods for validating an object (JSR 380).
 *
 * @since 1.0.0
 */
@Service
@Singleton
public final class Validation {

    // Validators do not return the arguments, but simply the value that was tested.
    // This list indicates arguments to be retrieved from the attributes.
    private static final List<String> SORTED_ARGUMENT_TO_EXTRACT_LIST = List.of(
        "value", "min", "max", "integer", "years", "months", "days", "hours", "minutes", "seconds", "millis", "nanos", "inclusive");

    private final Map<Locale, Validator> validatorPerLocaleMap;

    /**
     * Build a new instance.
     *
     * @since 1.0.0
     */
    public Validation() {

        this.validatorPerLocaleMap = new ConcurrentHashMap<>();
    }

    /**
     * Validates an object.
     *
     * @param objectToValidate The object to validate
     * @param <T>              Type of the object to validate
     * @return The validated object
     * @since 1.0.0
     */
    public <T> Validated<T> validate(final T objectToValidate) {

        return this.validate(objectToValidate, Locale.getDefault());
    }

    /**
     * Validates an object.
     *
     * @param objectToValidate     The object to validate
     * @param locale               The locale to use for validation error message
     * @param constraintGroupArray The constraint groups to apply (OPTIONAL)
     * @param <T>                  Type of the object to validate
     * @return The validated object
     * @since 1.2.0
     */
    public <T> Validated<T> validate(final T objectToValidate,
                                     final Locale locale,
                                     final Class<?>... constraintGroupArray) {

        final Map<String, List<ValidationError>> validationErrorPerKeyMap = new HashMap<>();

        // Object to validate is null, abord!
        if (objectToValidate == null) {
            return new Validated<>(null, validationErrorPerKeyMap);
        }

        // Retrieves the correct validator according to the locale and validates the object provided
        final Validator validator = this.validatorPerLocaleMap.computeIfAbsent(locale, this::createValidator);
        final Set<ConstraintViolation<T>> constraintViolationSet = validator.validate(objectToValidate, constraintGroupArray);

        constraintViolationSet.stream().sorted(Comparator.comparing(ConstraintViolation::getMessageTemplate)).forEach(constraintViolation -> {
            final String fieldKey = constraintViolation.getPropertyPath().toString();
            final List<ValidationError> validationErrorList = validationErrorPerKeyMap.computeIfAbsent(
                fieldKey,
                (key) -> new ArrayList<>());

            validationErrorList.add(
                new ValidationError(
                    constraintViolation.getMessage(),
                    constraintViolation.getMessageTemplate().replaceAll("[{}]", StringConstants.EMPTY),
                    createMessageArgumentArray(constraintViolation)));
        });

        // Result
        return new Validated<>(objectToValidate, validationErrorPerKeyMap);
    }

    /**
     * Creates message arguments array
     *
     * @param constraintViolation The constraint violation
     * @return The message arguments array
     * @since 1.0.0
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
     * @since 1.0.0
     */
    private Validator createValidator(final Locale locale) {

        Configuration<?> configuration = jakarta.validation.Validation.byDefaultProvider().configure();
        configuration = configuration.messageInterpolator(new MessageInterpolatorWithLocale(new ParameterMessageInterpolator(), locale));

        try (final ValidatorFactory validatorFactory = configuration.buildValidatorFactory()) {
            return validatorFactory.getValidator();
        }
    }

    /**
     * Message interpolator with a custom locale.
     *
     * @param delegate The base message interpolator
     * @param locale   The locale
     * @since 1.0.0
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
