package dev.voidframework.core.bindable;

import com.google.inject.Singleton;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated class can be bind. Such classes are considered
 * as candidates for auto-detection during the classpath scanning.
 */
@Singleton
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindClass {
}
