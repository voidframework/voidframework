package dev.voidframework.core.bindable;

import com.google.inject.Singleton;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated class is a bind-able. Such classes are considered
 * as candidates for auto-detection when using classpath scanning.
 */
@Singleton
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindClass {
}
