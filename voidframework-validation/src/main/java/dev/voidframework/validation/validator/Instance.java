package dev.voidframework.validation.validator;

import dev.voidframework.validation.validator.impl.InstanceImpl;
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
 * The annotated element class type must be compliant.
 *
 * @since 1.5.0
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = InstanceImpl.class)
public @interface Instance {

    /**
     * @return The constraint error message
     * @since 1.5.0
     */
    String message() default "{voidframework.validation.constraints.Instance.message}";

    /**
     * @return The subset of constraints
     * @since 1.5.0
     */
    Class<?>[] groups() default {};

    /**
     * @return The attached Payload type
     * @since 1.5.0
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * @return The allowed classes
     * @since 1.5.0
     */
    Class<?>[] value();

    /**
     * Defines several <code>@Instance</code> annotations on the same element
     *
     * @see Instance
     * @since 1.5.0
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        /**
         * @return Defined annotations
         * @since 1.5.0
         */
        Instance[] value();
    }
}
