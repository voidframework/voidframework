package dev.voidframework.web.http.filter;

import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Result;

/**
 * Represents a chain of filters to apply.
 *
 * @since 1.0.0
 */
public interface FilterChain {

    /**
     * Applies the next filter.
     *
     * @param context The current context
     * @return A result
     * @since 1.0.0
     */
    Result applyNext(final Context context);
}
