package dev.voidframework.validation.validator.impl;

import dev.voidframework.validation.validator.NotInstance;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementation of the annotation {@link NotInstance}.
 */
public class NotInstanceImpl implements ConstraintValidator<NotInstance, Object> {

    private NotInstance constraintAnnotation;

    @Override
    public void initialize(final NotInstance constraintAnnotation) {

        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        for (final Class<?> classType : constraintAnnotation.value()) {
            if (classType.isInstance(value)) {
                return false;
            }
        }

        return true;
    }
}
