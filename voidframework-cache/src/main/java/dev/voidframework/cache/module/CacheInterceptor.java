package dev.voidframework.cache.module;

import com.google.inject.Inject;
import dev.voidframework.cache.Cache;
import dev.voidframework.cache.engine.BlackHoleCacheEngine;
import dev.voidframework.cache.engine.CacheEngine;
import dev.voidframework.core.helper.ProxyDetector;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Intercepts method call when annotation {@link Cache} is used.
 */
public final class CacheInterceptor implements MethodInterceptor {

    private CacheEngine cacheEngine;

    /**
     * Build a new instance.
     */
    public CacheInterceptor() {
        this.cacheEngine = null;
    }

    /**
     * Sets the cache engine to use.
     *
     * @param cacheEngine The cache engine
     */
    @Inject
    public void setCacheEngine(final CacheEngine cacheEngine) {
        if (!(cacheEngine instanceof BlackHoleCacheEngine)) {
            this.cacheEngine = cacheEngine;
        }
    }

    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        if (cacheEngine == null) {
            return methodInvocation.proceed();
        }

        final Cache cache = methodInvocation.getMethod().getAnnotation(Cache.class);

        final String cacheKey;
        if (cache.key().contains("{")) {
            final String className = ProxyDetector.isProxy(methodInvocation.getThis())
                ? methodInvocation.getThis().getClass().getSuperclass().getName()
                : methodInvocation.getThis().getClass().getName();
            final String methodName = methodInvocation.getMethod().getName();
            cacheKey = cache.key()
                .replace("{class}", className)
                .replace("{method}", methodName);
        } else {
            cacheKey = cache.key();
        }

        Object value = this.cacheEngine.get(cacheKey);
        if (value == null) {
            value = methodInvocation.proceed();
            this.cacheEngine.set(cacheKey, value, cache.timeToLive());
        }

        return value;
    }
}
