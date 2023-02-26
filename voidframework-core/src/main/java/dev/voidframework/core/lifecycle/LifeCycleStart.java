package dev.voidframework.core.lifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the method should be called when the application is started.
 *
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LifeCycleStart {

    /**
     * Defines the start-up priority.
     *
     * @return The priority
     * @since 1.0.0
     */
    int priority() default 1000;
}
