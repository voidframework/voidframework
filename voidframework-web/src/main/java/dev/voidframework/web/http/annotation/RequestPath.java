package dev.voidframework.web.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which indicates that a method parameter should
 * be bound to a path segment variable.
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.PARAMETER)
public @interface RequestPath {

    /**
     * The name of the segment to bind to the method parameter.
     *
     * @return The name of the segment to bind to the method parameter.
     */
    String value();
}
