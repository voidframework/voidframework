package dev.voidframework.cache.module;

import dev.voidframework.cache.annotation.CachePut;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Intercepts method call when annotation {@link CachePut} is used.
 *
 * @since 1.7.0
 */
public final class CacheInterceptorPut extends CacheInterceptor {

    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {

        if (cacheEngine == null) {
            return methodInvocation.proceed();
        }

        final CachePut cachePut = methodInvocation.getMethod().getAnnotation(CachePut.class);
        final String cacheKey = resolveCacheKey(methodInvocation, cachePut.key());

        final Object value = methodInvocation.proceed();
        this.cacheEngine.set(cacheKey, value, cachePut.timeToLive());

        return value;
    }
}
