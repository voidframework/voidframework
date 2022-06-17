package dev.voidframework.web.filter;

import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Result;

/**
 * Represents a filter.
 */
public interface Filter {

    /**
     * Applies the filter.
     *
     * @param context     The current context
     * @param filterChain The owner filter chain
     * @return A result
     */
    Result apply(final Context context, final FilterChain filterChain);
}
