package dev.voidframework.validation.validator;

import dev.voidframework.validation.validator.impl.TrimmedLengthImpl;
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
 * The annotated element length must be between the specified boundaries (included).
 * Trailing and ending whitespaces are ignored.
 *
 * @since 1.0.0
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = TrimmedLengthImpl.class)
public @interface TrimmedLength {

    /**
     * @return The constraint error message
     * @since 1.0.0
     */
    String message() default "{org.hibernate.validator.constraints.Length.message}";

    /**
     * @return The subset of constraints
     * @since 1.0.0
     */
    Class<?>[] groups() default {};

    /**
     * @return The attached Payload type
     * @since 1.0.0
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * @return The size the element must be higher or equal to
     * @since 1.0.0
     */
    int min() default 0;

    /**
     * @return The size the element must be lowed or equal to
     * @since 1.0.0
     */
    int max() default Integer.MAX_VALUE;

    /**
     * Defines several <code>@TrimmedSize</code> annotations on the same element
     *
     * @see TrimmedLength
     * @since 1.0.0
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        /**
         * @return Defined annotations
         * @since 1.0.0
         */
        TrimmedLength[] value();
    }
}
