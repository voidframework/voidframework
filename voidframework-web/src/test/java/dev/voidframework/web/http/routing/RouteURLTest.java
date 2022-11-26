package dev.voidframework.web.http.routing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class RouteURLTest {

    static Stream<Arguments> sourceRouteURLTestMethodOf() {
        return Stream.of(
            Arguments.of("/context-path/prefix/route", "/context-path/", "/prefix", "/route"),
            Arguments.of("/context-path/prefix/route", "context-path", "prefix", "route"),
            Arguments.of("/route", "", "", "route"),
            Arguments.of("/route", "/", "/", "/route"),
            Arguments.of("/route", null, null, "route"),
            Arguments.of("/route", null, null, "/route/"),
            Arguments.of("/", null, null, null));
    }

    @ParameterizedTest
    @MethodSource("sourceRouteURLTestMethodOf")
    void of(final String expected, final String contextPath, final String prefix, final String route) {

        // Act
        final RouteURL routeURL = RouteURL.of(contextPath, prefix, route);

        // Assert
        Assertions.assertNotNull(routeURL);
        Assertions.assertEquals(expected, routeURL.url());
    }
}
