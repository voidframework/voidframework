package dev.voidframework.web.http.filter;

import dev.voidframework.web.exception.FilterException;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Result;

import java.util.List;

/**
 * Default implementation of {@link FilterChain}.
 *
 * @since 1.0.0
 */
public final class DefaultFilterChain implements FilterChain {

    private final List<Filter> filterList;
    private int currentFilterIndex;

    /**
     * Build a new instance.
     *
     * @param filterList Filters to apply
     * @since 1.0.0
     */
    public DefaultFilterChain(final List<Filter> filterList) {

        this.filterList = filterList;
        this.currentFilterIndex = -1;
    }

    @Override
    public Result applyNext(final Context context) {

        if (this.currentFilterIndex < this.filterList.size()) {
            this.currentFilterIndex += 1;

            return this.filterList.get(this.currentFilterIndex).apply(context, this);
        }

        throw new FilterException.Overflow(this.currentFilterIndex, this.filterList.size());
    }
}
