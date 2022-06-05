package dev.voidframework.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that the result of invoking a method can be cached.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    /**
     * Key of the cache in which result is stored.
     *
     * @return Key of the cache in which result is stored
     */
    String key() default "{class}::{method}";

    /**
     * Retention time (in seconds).
     *
     * @return Retention time (in seconds).
     */
    int timeToLive() default -1;
}
