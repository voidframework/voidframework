package dev.voidframework.cache.module;

import dev.voidframework.cache.annotation.CacheResult;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Intercepts method call when annotation {@link CacheResult} is used.
 *
 * @since 1.0.1
 */
public final class CacheInterceptorResult extends CacheInterceptor {

    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {

        if (cacheEngine == null) {
            return methodInvocation.proceed();
        }

        final CacheResult cacheResult = methodInvocation.getMethod().getAnnotation(CacheResult.class);
        final String cacheKey = resolveCacheKey(methodInvocation, cacheResult.key());

        Object value = this.cacheEngine.get(cacheKey);
        if (value == null) {
            value = methodInvocation.proceed();
            this.cacheEngine.set(cacheKey, value, cacheResult.timeToLive());
        }

        return value;
    }
}
