package dev.voidframework.web.http.filter;

import dev.voidframework.core.bindable.Controller;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines filters to apply.
 */
@Controller
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithFilter {

    /**
     * Defines filters to apply.
     *
     * @return Filters to apply
     */
    Class<? extends Filter>[] value() default {};
}
