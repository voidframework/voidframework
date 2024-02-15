package dev.voidframework.web.http.routing;

import dev.voidframework.core.utils.ReflectionUtils;
import dev.voidframework.web.http.HttpMethod;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.annotation.RequestPath;
import dev.voidframework.web.http.routing.impl.DefaultRouter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.lang.reflect.Method;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
final class RouterTest {

    @Test
    void addRoute() {

        // Arrange
        final Router router = new DefaultRouter();
        final Method method = ReflectionUtils.resolveMethod("displayHelloWorld", SampleController.class);

        // Act
        router.addRoute(HttpMethod.GET, RouteURL.of("/"), SampleController.class, method);

        // Assert
        final List<Route> routeList = router.getRoutesAsList();
        Assertions.assertNotNull(routeList);
        Assertions.assertEquals(1, routeList.size());

        final Route route = routeList.get(0);
        Assertions.assertNotNull(route);
        Assertions.assertEquals(HttpMethod.GET, route.httpMethod());
        Assertions.assertEquals("/", route.routePattern().pattern());
        Assertions.assertEquals(SampleController.class, route.controllerClassType());
        Assertions.assertEquals("displayHelloWorld", route.method().getName());
    }

    @Test
    void resolveRoute() {

        // Arrange
        final Router router = new DefaultRouter();
        final Method methodDisplay = ReflectionUtils.resolveMethod("displayHelloWorld", SampleController.class);
        final Method methodRegister = ReflectionUtils.resolveMethod("displayRegister", SampleController.class);
        router.addRoute(HttpMethod.GET, RouteURL.of("/"), SampleController.class, methodDisplay);
        router.addRoute(HttpMethod.GET, RouteURL.of("/register"), SampleController.class, methodRegister);

        // Act
        final ResolvedRoute resolvedRoute = router.resolveRoute(HttpMethod.GET, "/register");

        // Assert
        Assertions.assertNotNull(resolvedRoute);
        Assertions.assertEquals(SampleController.class, resolvedRoute.controllerClassType());
        Assertions.assertEquals("displayRegister", resolvedRoute.method().getName());
        Assertions.assertNotNull(resolvedRoute.extractedParameterValues());
        Assertions.assertEquals(0, resolvedRoute.extractedParameterValues().size());
    }

    @Test
    void resolveRouteWithRegularExpression() {

        // Arrange
        final Router router = new DefaultRouter();
        final Method methodDisplay = ReflectionUtils.resolveMethod("displayHelloWorld", SampleController.class);
        final Method methodRegister = ReflectionUtils.resolveMethod("displayRegister", SampleController.class);
        final Method methodAccount = ReflectionUtils.resolveMethod("displayAccount", SampleController.class);
        router.addRoute(HttpMethod.GET, RouteURL.of("/"), SampleController.class, methodDisplay);
        router.addRoute(HttpMethod.GET, RouteURL.of("/register"), SampleController.class, methodRegister);
        router.addRoute(HttpMethod.GET, RouteURL.of("/register/(?<accountId>[a-z]{0,36})"), SampleController.class, methodAccount);

        // Act
        final ResolvedRoute resolvedRoute = router.resolveRoute(HttpMethod.GET, "/register/toto");

        // Assert
        Assertions.assertNotNull(resolvedRoute);
        Assertions.assertEquals(SampleController.class, resolvedRoute.controllerClassType());
        Assertions.assertEquals("displayAccount", resolvedRoute.method().getName());
        Assertions.assertNotNull(resolvedRoute.extractedParameterValues());
        Assertions.assertEquals(1, resolvedRoute.extractedParameterValues().size());
        Assertions.assertTrue(resolvedRoute.extractedParameterValues().containsKey("accountId"));
        Assertions.assertEquals("toto", resolvedRoute.extractedParameterValues().get("accountId"));
    }

    @Test
    void resolveRouteWithSimplifiedVariable() {

        // Arrange
        final Router router = new DefaultRouter();
        final Method methodAccount = ReflectionUtils.resolveMethod("displayAccount", SampleController.class);
        router.addRoute(HttpMethod.GET, RouteURL.of("/register/{accountId}"), SampleController.class, methodAccount);

        // Act
        final ResolvedRoute resolvedRoute = router.resolveRoute(HttpMethod.GET, "/register/toto");

        // Assert
        Assertions.assertNotNull(resolvedRoute);
        Assertions.assertEquals(SampleController.class, resolvedRoute.controllerClassType());
        Assertions.assertEquals("displayAccount", resolvedRoute.method().getName());
        Assertions.assertNotNull(resolvedRoute.extractedParameterValues());
        Assertions.assertEquals(1, resolvedRoute.extractedParameterValues().size());
        Assertions.assertTrue(resolvedRoute.extractedParameterValues().containsKey("accountId"));
        Assertions.assertEquals("toto", resolvedRoute.extractedParameterValues().get("accountId"));
    }

    @Test
    void reverseUrlWithName() {

        // Arrange
        final Router router = new DefaultRouter();
        final Method methodAccount = ReflectionUtils.resolveMethod("displayAccount", SampleController.class);
        router.addRoute(HttpMethod.GET, RouteURL.of("/account/(?<accountId>[a-z0-9\\-]{36})"), SampleController.class, methodAccount, "account");

        // Act
        final String url = router.reverseRoute("account", List.of("33d7ed6b-9034-4305-8f51-950914f9b08f"));

        // Assert
        Assertions.assertNotNull(url);
        Assertions.assertEquals("/account/33d7ed6b-9034-4305-8f51-950914f9b08f", url);
    }

    @Test
    void reverseUrlWithoutName() {

        // Arrange
        final Router router = new DefaultRouter();
        final Method methodAccount = ReflectionUtils.resolveMethod("displayAccount", SampleController.class);
        router.addRoute(HttpMethod.GET, RouteURL.of("/account/(?<accountId>[a-z0-9\\-]{36})"), SampleController.class, methodAccount);

        // Act
        final String url = router.reverseRoute("dev.voidframework.web.http.routing.RouterTest.SampleController.displayAccount", List.of("33d7ed6b-9034-4305-8f51-950914f9b08f"));

        // Assert
        Assertions.assertNotNull(url);
        Assertions.assertEquals("/account/33d7ed6b-9034-4305-8f51-950914f9b08f", url);
    }

    @Test
    void reverseUrlBase() {

        // Arrange
        final Router router = new DefaultRouter();
        final Method methodHelloWorld = ReflectionUtils.resolveMethod("displayHelloWorld", SampleController.class);
        router.addRoute(HttpMethod.GET, RouteURL.of("/"), SampleController.class, methodHelloWorld, "helloworld");

        // Act
        final String url = router.reverseRoute("helloworld", List.of());

        // Assert
        Assertions.assertNotNull(url);
        Assertions.assertEquals("/", url);
    }

    @Test
    void reverseUrlNoArguments() {

        // Arrange
        final Router router = new DefaultRouter();
        final Method methodHelloWorld = ReflectionUtils.resolveMethod("displayHelloWorld", SampleController.class);
        router.addRoute(HttpMethod.GET, RouteURL.of("/test"), SampleController.class, methodHelloWorld, "helloworld");

        // Act
        final String url = router.reverseRoute("helloworld", List.of());

        // Assert
        Assertions.assertNotNull(url);
        Assertions.assertEquals("/test", url);
    }

    /**
     * A simple controller.
     */
    @SuppressWarnings("unused")
    private static final class SampleController {

        public Result displayHelloWorld() {

            return Result.ok("Hello World!");
        }

        public Result displayRegister() {

            return Result.ok("Register Form");
        }

        public Result displayAccount(final @RequestPath("accountId") String accountId) {

            return Result.ok("My Account ID is " + accountId);
        }

        void returnNothing() {
        }
    }
}
