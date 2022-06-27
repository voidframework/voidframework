package dev.voidframework.web.http.param;

import dev.voidframework.web.routing.HttpMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which indicates that the method should
 * be called if the incoming request matches.
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface RequestRoute {

    /**
     * Returns the HTTP method (ie: GET).
     *
     * @return The HTTP method
     */
    HttpMethod method() default HttpMethod.GET;

    /**
     * Returns the route URL (ie: /test/helloworld).
     *
     * @return The route URL
     */
    String route() default "/";

    /**
     * Returns the route name.
     * This name is used during the reverse URL process.
     *
     * @return The route name
     */
    String name() default "";
}
