package dev.voidframework.validation.validator.impl;

import dev.voidframework.validation.validator.CUID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementation of the annotation {@link CUID}.
 *
 * @since 1.5.0
 */
public class CUIDAsStringImpl implements ConstraintValidator<CUID, String> {

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        return dev.voidframework.core.lang.CUID.isValid(value);
    }
}
