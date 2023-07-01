package dev.voidframework.bucket4j.module;

import dev.voidframework.bucket4j.BucketTokenRegistry;
import dev.voidframework.bucket4j.annotation.BucketToken;
import dev.voidframework.bucket4j.exception.BucketTokenException;
import io.github.bucket4j.Bucket;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Intercepts method calls to apply "bucket-token" algorithm.
 *
 * @since 1.9.0
 */
public class BucketTokenInterceptor implements MethodInterceptor {

    private final BucketTokenRegistry bucketTokenRegistry;

    /**
     * Build a new instance.
     *
     * @param bucketTokenRegistry The Bucket registry
     * @since 1.9.0
     */
    public BucketTokenInterceptor(final BucketTokenRegistry bucketTokenRegistry) {

        this.bucketTokenRegistry = bucketTokenRegistry;
    }

    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {

        // Retrieve annotation
        BucketToken bucketTokenAnnotation = methodInvocation.getMethod().getAnnotation(BucketToken.class);
        if (bucketTokenAnnotation == null) {
            bucketTokenAnnotation = methodInvocation.getThis().getClass().getAnnotation(BucketToken.class);
        }

        // Retrieve bucket
        final Bucket bucket = this.bucketTokenRegistry.bucket(bucketTokenAnnotation.value());
        if (bucket == null) {
            throw new BucketTokenException.BucketDoesNotExist(bucketTokenAnnotation.value());
        }

        // Try to enter protected method
        if (bucket.tryConsume(bucketTokenAnnotation.tokenToConsume())) {
            return methodInvocation.proceed();
        }

        // Can't enter protected method
        final Exception exceptionToThrow = new BucketTokenException.NoEnoughTokensAvailable(bucketTokenAnnotation.value());

        // Try to run fallback method (if provided)
        if (StringUtils.isNotBlank(bucketTokenAnnotation.fallbackMethod())) {
            final Method fallbackMethod = this.retrieveBestMatchingFallbackMethod(
                bucketTokenAnnotation.fallbackMethod(),
                methodInvocation.getThis().getClass());

            if (fallbackMethod != null) {
                return fallbackMethod.getParameterCount() == 1
                    ? fallbackMethod.invoke(methodInvocation.getThis(), exceptionToThrow)
                    : fallbackMethod.invoke(methodInvocation.getThis());
            }
        }

        // No other alternatives, we're throwing an exception
        throw exceptionToThrow;
    }

    /**
     * Searches the best matching fallback method.
     *
     * @param fallbackMethodName The fallback method name
     * @param classType          The type of the class containing the method
     * @return The best matching fallback method
     * @since 1.9.0
     */
    private Method retrieveBestMatchingFallbackMethod(final String fallbackMethodName, final Class<?> classType) {

        Method fallbackMethod = null;
        for (final Method method : classType.getMethods()) {
            if (this.isCandidate(fallbackMethodName, method)
                && (fallbackMethod == null || method.getParameterCount() > fallbackMethod.getParameterCount())) {
                fallbackMethod = method;
            }
        }

        return fallbackMethod;
    }

    /**
     * Checks if the given method is a good candidate.
     *
     * @param fallbackMethodName The fallback method name
     * @param method             The method to checks
     * @return {@code true} if the given method is a good candidate, otherwise {@code false}
     * @since 1.9.0
     */
    private boolean isCandidate(final String fallbackMethodName, final Method method) {

        if (!Objects.equals(fallbackMethodName, method.getName())) {
            return false;
        }

        return (method.getParameterCount() == 0)
            || (method.getParameterCount() == 1 && method.getParameterTypes()[0] == BucketTokenException.NoEnoughTokensAvailable.class);
    }
}
