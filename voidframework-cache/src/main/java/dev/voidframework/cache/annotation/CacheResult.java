package dev.voidframework.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that the result of the method will be cached and reused in future calls.
 *
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheResult {

    /**
     * Key of the cache in which result is stored.
     *
     * @return Key of the cache in which result is stored
     * @since 1.0.0
     */
    String key() default "{class}.{method}";

    /**
     * Retention time (in seconds).
     *
     * @return Retention time (in seconds)
     * @since 1.0.0
     */
    int timeToLive() default -1;
}
