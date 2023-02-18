package dev.voidframework.web.http.filter;

import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Result;

/**
 * Represents a filter.
 *
 * @since 1.0.0
 */
public interface Filter {

    /**
     * Applies the filter.
     *
     * @param context     The current context
     * @param filterChain The owner filter chain
     * @return A result
     * @since 1.0.0
     */
    Result apply(final Context context, final FilterChain filterChain);
}
