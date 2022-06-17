package dev.voidframework.web.filter;

import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Result;

/**
 * Represents a chain of filters to apply.
 */
public interface FilterChain {

    /**
     * Applies the next filter.
     *
     * @param context The current context
     * @return A result
     */
    Result applyNext(final Context context);
}
