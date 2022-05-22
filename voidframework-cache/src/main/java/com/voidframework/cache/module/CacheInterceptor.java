package com.voidframework.cache.module;

import com.google.inject.Inject;
import com.voidframework.cache.Cache;
import com.voidframework.cache.engine.CacheEngine;
import com.voidframework.core.helper.ProxyDetector;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nullable;

public class CacheInterceptor implements MethodInterceptor {

    private CacheEngine cacheEngine;

    /**
     * Build a new instance.
     */
    public CacheInterceptor() {
        this.cacheEngine = null;
    }

    @Inject
    public void setCacheEngine(@Nullable final CacheEngine cacheEngine) {
        this.cacheEngine = cacheEngine;
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
            this.cacheEngine.set(cacheKey, value, -1);
        }

        return value;
    }
}
