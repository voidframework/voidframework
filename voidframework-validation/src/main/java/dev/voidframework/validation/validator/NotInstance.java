package dev.voidframework.validation.validator;

import dev.voidframework.validation.validator.impl.NotInstanceImpl;
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
 * The annotated element class type must not be compliant.
 *
 * @since 1.6.0
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NotInstanceImpl.class)
public @interface NotInstance {

    /**
     * @return The constraint error message
     * @since 1.6.0
     */
    String message() default "{voidframework.validation.constraints.NotInstance.message}";

    /**
     * @return The subset of constraints
     * @since 1.6.0
     */
    Class<?>[] groups() default {};

    /**
     * @return The attached Payload type
     * @since 1.6.0
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * @return The allowed classes
     * @since 1.6.0
     */
    Class<?>[] value();

    /**
     * Defines several <code>@NotInstance</code> annotations on the same element
     *
     * @see NotInstance
     * @since 1.6.0
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        /**
         * @return Defined annotations
         * @since 1.6.0
         */
        NotInstance[] value();
    }
}
