package dev.voidframework.web.http.annotation;

import dev.voidframework.core.bindable.Controller;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that CSRF token verification don't have to be performed.
 *
 * @since 1.1.0
 */
@Controller
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoCSRF {
}
