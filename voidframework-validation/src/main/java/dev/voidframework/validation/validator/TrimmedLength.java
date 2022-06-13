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
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = TrimmedLengthImpl.class)
public @interface TrimmedLength {

    /**
     * @return The constraint error message
     */
    String message() default "{org.hibernate.validator.constraints.Length.message}";

    /**
     * @return The subset of constraints
     */
    Class<?>[] groups() default {};

    /**
     * @return The attached Payload type
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * @return The size the element must be higher or equal to
     */
    int min() default 0;

    /**
     * @return The size the element must be lower or equal to
     */
    int max() default Integer.MAX_VALUE;

    /**
     * Defines several <code>@TrimmedSize</code> annotations on the same element
     *
     * @see TrimmedLength
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        /**
         * @return Defined annotations.
         */
        TrimmedLength[] value();
    }
}
