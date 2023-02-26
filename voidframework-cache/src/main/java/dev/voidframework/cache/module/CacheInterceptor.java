package dev.voidframework.cache.module;

import com.google.inject.Inject;
import dev.voidframework.cache.engine.BlackHoleCacheEngine;
import dev.voidframework.cache.engine.CacheEngine;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.utils.ProxyDetectorUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Intercepts method calls to apply the desired cache behavior.
 *
 * @since 1.0.0
 */
public abstract class CacheInterceptor implements MethodInterceptor {

    /**
     * Cache engine to use.
     *
     * @since 1.0.0
     */
    protected CacheEngine cacheEngine;

    /**
     * Build a new instance.
     *
     * @since 1.0.0
     */
    protected CacheInterceptor() {

        this.cacheEngine = null;
    }

    /**
     * Sets the cache engine to use.
     *
     * @param cacheEngine The cache engine
     * @since 1.0.0
     */
    @Inject
    public void setCacheEngine(final CacheEngine cacheEngine) {

        if (!(cacheEngine instanceof BlackHoleCacheEngine)) {
            this.cacheEngine = cacheEngine;
        }
    }

    /**
     * Resolves the cache key.
     *
     * @param methodInvocation The current method invocation
     * @param keyPattern       The key pattern
     * @return The resolved cache key
     * @since 1.0.0
     */
    protected String resolveCacheKey(final MethodInvocation methodInvocation, final String keyPattern) {

        String cacheKey = keyPattern;

        if (cacheKey.isEmpty()) {
            cacheKey = "{class}.{method}";
        }

        if (cacheKey.contains(StringConstants.CURLY_BRACKET_OPEN)) {
            final String className = ProxyDetectorUtils.isProxy(methodInvocation.getThis())
                ? methodInvocation.getThis().getClass().getSuperclass().getName()
                : methodInvocation.getThis().getClass().getName();
            final String methodName = methodInvocation.getMethod().getName();

            cacheKey = keyPattern
                .replace("{class}", className)
                .replace("{method}", methodName);

            final Object[] argumentArray = methodInvocation.getArguments();
            for (int idx = 0; idx < argumentArray.length; idx += 1) {
                if (argumentArray[idx] == null) {
                    cacheKey = cacheKey.replace(StringConstants.CURLY_BRACKET_OPEN + idx + StringConstants.CURLY_BRACKET_CLOSE, "null");
                } else {
                    cacheKey = cacheKey.replace(StringConstants.CURLY_BRACKET_OPEN + idx + StringConstants.CURLY_BRACKET_CLOSE, argumentArray[idx].toString());
                }
            }
        }

        return cacheKey;
    }
}
