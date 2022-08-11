package dev.voidframework.core.filter;

import com.google.common.base.Charsets;
import dev.voidframework.web.filter.DefaultFilterChain;
import dev.voidframework.web.filter.Filter;
import dev.voidframework.web.filter.FilterChain;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.FlashMessages;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class FilterTest {

    @Test
    public void filterChain() throws IOException {

        // Arrange
        final List<Filter> filterList = List.of(
            (ctx, filterChain) -> filterChain.applyNext(ctx).withHeader("New-Header-2", "Value2"),
            (ctx, filterChain) -> filterChain.applyNext(ctx).withHeader("New-Header", "Value"),
            (ctx, filterChain) -> Result.ok("Hello World!"));
        final FilterChain filterChain = new DefaultFilterChain(filterList);

        final Context context = new Context(null, new Session(), new FlashMessages(), Locale.ENGLISH);

        // Act
        final Result result = filterChain.applyNext(context);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(200, result.getHttpCode());
        Assertions.assertEquals(2, result.getHeaders().size());
        Assertions.assertEquals("Value", result.getHeaders().get("New-Header"));
        Assertions.assertEquals("Value2", result.getHeaders().get("New-Header-2"));
        Assertions.assertEquals("Hello World!", new String(result.getResultProcessor().getInputStream().readAllBytes(), Charsets.UTF_8));
    }
}
