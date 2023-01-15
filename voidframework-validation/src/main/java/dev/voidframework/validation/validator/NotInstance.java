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
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NotInstanceImpl.class)
public @interface NotInstance {

    /**
     * @return The constraint error message
     */
    String message() default "{voidframework.validation.constraints.NotInstance.message}";

    /**
     * @return The subset of constraints
     */
    Class<?>[] groups() default {};

    /**
     * @return The attached Payload type
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * @return The allowed classes
     */
    Class<?>[] value();

    /**
     * Defines several <code>@NotInstance</code> annotations on the same element
     *
     * @see NotInstance
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        /**
         * @return Defined annotations.
         */
        NotInstance[] value();
    }
}
