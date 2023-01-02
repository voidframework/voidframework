package dev.voidframework.validation.validator;

import dev.voidframework.validation.validator.impl.CUIDAsByteArrayImpl;
import dev.voidframework.validation.validator.impl.CUIDAsStringImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element must be a valid CUID.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {CUIDAsStringImpl.class, CUIDAsByteArrayImpl.class})
public @interface CUID {

    /**
     * @return The constraint error message
     */
    String message() default "{voidframework.validation.constraints.CUID.message}";

    /**
     * @return The subset of constraints
     */
    Class<?>[] groups() default {};

    /**
     * @return The attached Payload type
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several <code>@CUID</code> annotations on the same element
     *
     * @see CUID
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        /**
         * @return Defined annotations.
         */
        CUID[] value();
    }
}
