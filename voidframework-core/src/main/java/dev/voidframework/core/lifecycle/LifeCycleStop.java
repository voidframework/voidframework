package dev.voidframework.core.lifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LifeCycleStop {

    /**
     * Defines priority when stopping.
     */
    int priority() default 1000;

    /**
     * Defines the configuration key for the time (in milliseconds) to shut down properly.
     */
    String gracefulStopTimeoutConfigKey() default "";
}
