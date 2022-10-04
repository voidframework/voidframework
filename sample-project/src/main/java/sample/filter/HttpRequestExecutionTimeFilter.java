package sample.filter;

import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.filter.Filter;
import dev.voidframework.web.http.filter.FilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple filter to print http request execution time.
 */
public class HttpRequestExecutionTimeFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestExecutionTimeFilter.class);

    @Override
    public Result apply(final Context context, final FilterChain filterChain) {
        final long startTime = System.nanoTime();
        final Result result = filterChain.applyNext(context);
        final long endTime = System.nanoTime();

        LOGGER.info("[{}]\t{} {} took {}ms",
            context.getRequest().getRemoteHostName(),
            context.getRequest().getHttpMethod(),
            context.getRequest().getRequestURI(),
            (endTime - startTime) / 1000000);

        return result;
    }
}
