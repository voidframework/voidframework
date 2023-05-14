package dev.voidframework.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that the cache must be evicted.
 *
 * @since 1.0.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheRemove {

    /**
     * Key of the cache to evict.
     *
     * @return Key of the cache
     * @since 1.0.1
     */
    String key() default "{class}.{method}";

    /**
     * Defines exception classes indicating the exception types that must cause a cache eviction. If
     * classes are specified, the cache will only be evicted if the specified exceptions are thrown.
     *
     * @return Class[] of Exceptions
     * @since 1.0.1
     */
    Class<?>[] evictOn() default {};

    /**
     * Defines exception Classes indicating the exception types that must not cause a cache eviction.
     *
     * @return Class[] of Exceptions
     * @since 1.0.1
     */
    Class<?>[] noEvictOn() default {};
}
