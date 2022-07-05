package dev.voidframework.cache.module;

import dev.voidframework.cache.CacheRemove;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Intercepts method call when annotation {@link CacheRemove} is used.
 */
public final class CacheInterceptorRemove extends CacheInterceptor {

    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {

        if (cacheEngine == null) {
            return methodInvocation.proceed();
        }

        final CacheRemove cacheRemove = methodInvocation.getMethod().getAnnotation(CacheRemove.class);
        final String cacheKey = resolveCacheKey(methodInvocation, cacheRemove.key());

        try {
            final Object value = methodInvocation.proceed();
            if (cacheRemove.evictOn().length == 0) {
                this.cacheEngine.remove(cacheKey);
            }
            return value;
        } catch (final Throwable throwable) {
            if (this.hasToEvict(cacheRemove, throwable.getClass())) {
                this.cacheEngine.remove(cacheKey);
            }
            throw throwable;
        }
    }

    /**
     * Determines whether an eviction should be performed.
     *
     * @param cacheRemove    The current cache annotation
     * @param throwableClass The caught throwable class
     * @return {@code true} if an eviction should be performed, otherwise {@code false}
     */
    private boolean hasToEvict(final CacheRemove cacheRemove, final Class<?> throwableClass) {

        // "noEvictFor" always takes precedence
        for (final Class<?> noEvictOn : cacheRemove.noEvictOn()) {
            if (noEvictOn == throwableClass) {
                return false;
            }
        }

        // If the list is empty, you simply need to evict
        if (cacheRemove.evictOn().length == 0) {
            return true;
        }

        // But on the other hand, if it is not, eviction is only allowed
        // if the exception class is present in the list
        for (final Class<?> noEvictOn : cacheRemove.evictOn()) {
            if (noEvictOn == throwableClass) {
                return true;
            }
        }

        return false;
    }
}
