package dev.voidframework.web.http.param;

import dev.voidframework.web.routing.HttpMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface RequestRoute {

    HttpMethod method();

    String route();

    String alias() default "";
}
