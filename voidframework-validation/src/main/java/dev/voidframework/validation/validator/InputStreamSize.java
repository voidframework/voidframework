package dev.voidframework.validation.validator;

import dev.voidframework.validation.validator.impl.InputStreamSizeImpl;
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
 * The annotated {@code InputStream} size must be between the specified boundaries (included).
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = InputStreamSizeImpl.class)
public @interface InputStreamSize {

    /**
     * @return The constraint error message
     */
    String message() default "{jakarta.validation.constraints.Size.message}";

    /**
     * @return The subset of constraints
     */
    Class<?>[] groups() default {};

    /**
     * @return The attached Payload type
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * @return The number of available bytes must be higher or equal to
     */
    long min() default 0;

    /**
     * @return The number of available bytes must be lower or equal to
     */
    long max() default Long.MAX_VALUE;

    /**
     * Defines several <code>@InputStreamSize</code> annotations on the same element
     *
     * @see InputStreamSize
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        /**
         * @return Defined annotations.
         */
        InputStreamSize[] value();
    }
}
