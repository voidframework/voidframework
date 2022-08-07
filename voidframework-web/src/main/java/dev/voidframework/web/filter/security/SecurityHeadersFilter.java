package dev.voidframework.web.filter.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.web.filter.Filter;
import dev.voidframework.web.filter.FilterChain;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * Adds some security headers.
 */
@Singleton
public final class SecurityHeadersFilter implements Filter {

    private final Map<String, String> headerMap;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    @Inject
    public SecurityHeadersFilter(final Config configuration) {

        this.headerMap = new HashMap<>();
        this.headerMap.put("X-Content-Type-Options", configuration.getString("voidframework.web.securityHeaders.contentTypeOptions"));
        this.headerMap.put("X-Frame-Options", configuration.getString("voidframework.web.securityHeaders.frameOptions"));
        this.headerMap.put("X-XSS-Protection", configuration.getString("voidframework.web.securityHeaders.xssProtection"));
        this.headerMap.put("Cross-Origin-Resource-Policy", configuration.getString("voidframework.web.securityHeaders.crossOriginResourcePolicy"));
        this.headerMap.put("Content-Security-Policy", configuration.getString("voidframework.web.securityHeaders.contentSecurityPolicy"));
    }

    @Override
    public Result apply(final Context context, final FilterChain filterChain) {

        return filterChain.applyNext(context)
            .withHeaders(this.headerMap);
    }
}
