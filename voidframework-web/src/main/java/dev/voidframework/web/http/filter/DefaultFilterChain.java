package dev.voidframework.web.http.filter;

import dev.voidframework.web.exception.FilterException;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Result;

import java.util.List;

/**
 * Default implementation of {@link FilterChain}.
 */
public final class DefaultFilterChain implements FilterChain {

    private final List<Filter> filterList;
    private int currentFilterIndex;

    /**
     * Build a new instance.
     *
     * @param filterList Filters to apply
     */
    public DefaultFilterChain(final List<Filter> filterList) {

        this.filterList = filterList;
        this.currentFilterIndex = -1;
    }

    /**
     * Applies the next filter.
     *
     * @param context The current context
     * @return A result
     */
    @Override
    public Result applyNext(final Context context) {

        if (this.currentFilterIndex < this.filterList.size()) {
            this.currentFilterIndex += 1;

            return this.filterList.get(this.currentFilterIndex).apply(context, this);
        }

        throw new FilterException.Overflow(this.currentFilterIndex, this.filterList.size());
    }
}
