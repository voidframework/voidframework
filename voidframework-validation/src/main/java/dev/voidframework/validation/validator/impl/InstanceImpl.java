package dev.voidframework.validation.validator.impl;

import dev.voidframework.validation.validator.Instance;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementation of the annotation {@link Instance}.
 *
 * @since 1.5.0
 */
public class InstanceImpl implements ConstraintValidator<Instance, Object> {

    private Instance constraintAnnotation;

    @Override
    public void initialize(final Instance constraintAnnotation) {

        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        for (final Class<?> classType : constraintAnnotation.value()) {
            if (classType.isInstance(value)) {
                return true;
            }
        }

        return false;
    }
}
