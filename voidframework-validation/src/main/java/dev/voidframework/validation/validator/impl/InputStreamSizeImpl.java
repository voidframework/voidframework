package dev.voidframework.validation.validator.impl;

import dev.voidframework.validation.validator.InputStreamSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of the annotation {@link InputStreamSize}.
 *
 * @since 1.0.0
 */
public class InputStreamSizeImpl implements ConstraintValidator<InputStreamSize, InputStream> {

    private InputStreamSize constraintAnnotation;

    @Override
    public void initialize(final InputStreamSize constraintAnnotation) {

        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(final InputStream value, final ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        try {
            final long availableBytes = value.available();
            return availableBytes >= constraintAnnotation.min() && availableBytes <= constraintAnnotation.max();
        } catch (final IOException ignore) {
            return false;
        }
    }
}
