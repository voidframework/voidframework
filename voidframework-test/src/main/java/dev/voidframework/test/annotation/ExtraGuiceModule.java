package dev.voidframework.test.annotation;

import com.google.inject.Module;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates extra Guice modules to load when the VoidFramework is started up.
 *
 * @since 1.2.0
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ExtraGuiceModule {

    /**
     * An array of one or more Module classes to register.
     *
     * @since 1.2.0
     */
    Class<? extends Module>[] value() default {};
}
