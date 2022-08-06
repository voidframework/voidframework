package dev.voidframework.web.filter.security;

import com.google.inject.Singleton;
import dev.voidframework.web.filter.Filter;
import dev.voidframework.web.filter.FilterChain;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Result;

/**
 * Adds some security headers.
 */
@Singleton
public final class SecurityHeaderFilter implements Filter {

    @Override
    public Result apply(final Context context, final FilterChain filterChain) {

        return filterChain.applyNext(context)
            .withHeader("X-Content-Type-Options", "nosniff")
            .withHeader("X-Frame-Options", "DENY")
            .withHeader("X-XSS-Protection", "1; mode=block")
            .withHeader("Cross-Origin-Resource-Policy", "same-origin");
    }
}
