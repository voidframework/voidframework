package dev.voidframework.bucket4j.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that the current annotated method or all methods of the
 * annotated class are protected with "Token-bucket" rate-limiting algorithm. If
 * no fallback method was specified, the exception {@code NoEnoughTokensAvailable}
 * will be thrown.
 *
 * @see dev.voidframework.bucket4j.exception.BucketTokenException.NoEnoughTokensAvailable BucketTokenException.NoEnoughTokensAvailable
 * @see <a href="https://github.com/bucket4j/bucket4j">Bucket4J</a>
 * @since 1.9.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface BucketToken {

    /**
     * Name of the bucket to be used.
     * Bucket must be configured or an exception will be thrown.
     *
     * @return Name of the bucket to be used
     * @since 1.9.0
     */
    String value();

    /**
     * Name of the fallback method to use when "Token-bucket" rate-limiting
     * algorithm block access to the protected method. If there are multiple
     * methods matching given name, the method that has the most-closest match
     * will be invoked. The method could have no arguments or only one: the
     * exception {@code NoEnoughTokensAvailable}.
     *
     * @return Name of the fallback method
     * @see dev.voidframework.bucket4j.exception.BucketTokenException.NoEnoughTokensAvailable BucketTokenException.NoEnoughTokensAvailable
     * @since 1.9.0
     */
    String fallbackMethod() default "";

    /**
     * Number of token to consume to execute protected method.
     *
     * @return Number of token to consume
     * @since 1.9.0
     */
    int tokenToConsume() default 1;
}
