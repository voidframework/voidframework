package dev.voidframework.web.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which indicates that a method parameter should
 * be bound to a query string variable.
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.PARAMETER)
public @interface RequestVariable {

    /**
     * The name of the query string variable to bind to the method parameter.
     *
     * @return The name of the query string variable to bind to the method parameter.
     */
    String value();

    /**
     * The fallback value to use when the request variable is not provided.
     *
     * @return The fallback value to use when the request variable is not provided
     */
    String fallback() default "\00\00";
}
