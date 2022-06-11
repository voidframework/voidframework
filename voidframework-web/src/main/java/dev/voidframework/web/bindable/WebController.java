package dev.voidframework.web.bindable;

import dev.voidframework.core.bindable.Controller;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated class is a web controller.
 */
@Controller
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebController {

    /**
     * Defines a prefix that will be applied to all routes defined in the annotated class.
     *
     * @return The prefix to apply
     */
    String prefixRoute() default "";
}
