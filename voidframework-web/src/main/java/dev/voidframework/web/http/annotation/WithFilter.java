package dev.voidframework.web.http.annotation;

import dev.voidframework.core.bindable.Controller;
import dev.voidframework.web.http.filter.Filter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines filters to apply.
 *
 * @since 1.0.0
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
     * @since 1.0.0
     */
    Class<? extends Filter>[] value() default {};
}
