package dev.voidframework.core.lifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the method should be called when the application is stopped.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LifeCycleStop {

    /**
     * Defines priority when stopping.
     *
     * @return The priority
     */
    int priority() default 1000;

    /**
     * Defines the configuration key for the time (in milliseconds) to shut down properly.
     *
     * @return The configuration key
     */
    String gracefulStopTimeoutConfigKey() default "";
}
