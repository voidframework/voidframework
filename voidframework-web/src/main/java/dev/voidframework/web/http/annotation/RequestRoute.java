package dev.voidframework.web.http.annotation;

import dev.voidframework.web.http.HttpMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which indicates that the method should
 * be called if the incoming request matches.
 *
 * @since 1.0.0
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface RequestRoute {

    /**
     * Returns the HTTP method (ie: GET).
     *
     * @return The HTTP method
     * @since 1.0.0
     */
    HttpMethod method() default HttpMethod.GET;

    /**
     * Returns the route URL (ie: /test/helloworld).
     *
     * @return The route URL
     * @since 1.0.0
     */
    String route() default "/";

    /**
     * Returns the route name.
     * This name is used during the reverse URL process.
     *
     * @return The route name
     * @since 1.0.0
     */
    String name() default "";
}
