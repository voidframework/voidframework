package dev.voidframework.validation.validator.impl;

import dev.voidframework.validation.validator.CUID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.nio.charset.StandardCharsets;

/**
 * Implementation of the annotation {@link CUID}.
 *
 * @since 1.5.0
 */
public class CUIDAsByteArrayImpl implements ConstraintValidator<CUID, byte[]> {

    @Override
    public boolean isValid(final byte[] value, final ConstraintValidatorContext context) {

        if (value == null || value.length == 0) {
            return true;
        }

        return dev.voidframework.core.lang.CUID.isValid(new String(value, StandardCharsets.UTF_8));
    }
}
