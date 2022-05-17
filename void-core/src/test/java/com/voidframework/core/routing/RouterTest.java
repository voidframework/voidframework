package com.voidframework.core.routing;

import com.voidframework.core.exception.RoutingException;
import com.voidframework.core.http.RequestPath;
import com.voidframework.core.http.Result;
import com.voidframework.core.routing.impl.DefaultRouter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class RouterTest {

    @Test
    public void addRoute() {
        final Router router = new DefaultRouter();
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/").call(SampleController.class, "displayHelloWorld"));

        final List<Route> routeList = router.getRoutesAsList();
        Assertions.assertNotNull(routeList);
        Assertions.assertEquals(1, routeList.size());

        final Route route = routeList.get(0);
        Assertions.assertNotNull(route);
        Assertions.assertEquals(HttpMethod.GET, route.httpMethod);
        Assertions.assertEquals("/", route.routePattern.pattern());
        Assertions.assertEquals(SampleController.class, route.controllerClass);
        Assertions.assertEquals("displayHelloWorld", route.method.getName());
    }

    @Test
    public void addRoute_missingArgument() {
        RoutingException.Missing thrown;

        thrown = Assertions.assertThrows(RoutingException.Missing.class, () -> {
            final Router router = new DefaultRouter();
            router.addRoute(routeBuilder -> routeBuilder.route("/").call(SampleController.class, "displayHelloWorld"));
        });
        Assertions.assertEquals("Value 'method' is missing", thrown.getMessage());

        thrown = Assertions.assertThrows(RoutingException.Missing.class, () -> {
            final Router router = new DefaultRouter();
            router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).call(SampleController.class, "displayHelloWorld"));
        });
        Assertions.assertEquals("Value 'route' is missing", thrown.getMessage());

        thrown = Assertions.assertThrows(RoutingException.Missing.class, () -> {
            final Router router = new DefaultRouter();
            router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/"));
        });
        Assertions.assertEquals("Value 'controllerClass' is missing", thrown.getMessage());

        thrown = Assertions.assertThrows(RoutingException.Missing.class, () -> {
            final Router router = new DefaultRouter();
            router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/").call(SampleController.class, null));
        });
        Assertions.assertEquals("Value 'methodName' is missing", thrown.getMessage());
    }

    @Test
    public void addRoute_badRoutePattern() {
        final RoutingException.BadValue thrown = Assertions.assertThrows(RoutingException.BadValue.class, () -> {
            final Router router = new DefaultRouter();
            router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("[-").call(SampleController.class, "displayHelloWorld"));
        });
        Assertions.assertEquals("Invalid value 'route': Can't compile regular expression", thrown.getMessage());
    }

    @Test
    public void addRoute_controllerMethodDoesNotExists() {
        final RoutingException.ControllerMethodDoesNotExists thrown = Assertions.assertThrows(
            RoutingException.ControllerMethodDoesNotExists.class,
            () -> {
                final Router router = new DefaultRouter();
                router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/").call(SampleController.class, "unknownMethodName"));
            });
        Assertions.assertEquals("Method 'com.voidframework.core.routing.RouterTest$SampleController::unknownMethodName' with 0 parameter(s) does not exists", thrown.getMessage());
    }

    @Test
    public void addRoute_controllerMethodDoesNotReturnsValue() {
        final RoutingException.ControllerMethodMustReturnResult thrown = Assertions.assertThrows(
            RoutingException.ControllerMethodMustReturnResult.class,
            () -> {
                final Router router = new DefaultRouter();
                router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/").call(SampleController.class, "returnNothing"));
            });
        Assertions.assertEquals("Method 'com.voidframework.core.routing.RouterTest$SampleController::returnNothing' must return a Result", thrown.getMessage());
    }

    @Test
    public void resolveRoute() {
        final Router router = new DefaultRouter();
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/").call(SampleController.class, "displayHelloWorld"));
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/register").call(SampleController.class, "displayRegister"));

        final ResolvedRoute resolvedRoute = router.resolveRoute(HttpMethod.GET, "/register");
        Assertions.assertNotNull(resolvedRoute);
        Assertions.assertEquals(SampleController.class, resolvedRoute.controllerClass());
        Assertions.assertEquals("displayRegister", resolvedRoute.method().getName());
        Assertions.assertNotNull(resolvedRoute.extractedParameterValues());
        Assertions.assertEquals(0, resolvedRoute.extractedParameterValues().size());
    }

    @Test
    public void resolveRoute_withRegularExpression() {
        final Router router = new DefaultRouter();
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/").call(SampleController.class, "displayHelloWorld"));
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/register").call(SampleController.class, "displayRegister"));
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/register/(?<accountId>[a-z]{0,36})").call(SampleController.class, "displayAccount"));

        final ResolvedRoute resolvedRoute = router.resolveRoute(HttpMethod.GET, "/register/toto");
        Assertions.assertNotNull(resolvedRoute);
        Assertions.assertEquals(SampleController.class, resolvedRoute.controllerClass());
        Assertions.assertEquals("displayAccount", resolvedRoute.method().getName());
        Assertions.assertNotNull(resolvedRoute.extractedParameterValues());
        Assertions.assertEquals(1, resolvedRoute.extractedParameterValues().size());
        Assertions.assertTrue(resolvedRoute.extractedParameterValues().containsKey("accountId"));
        Assertions.assertEquals("toto", resolvedRoute.extractedParameterValues().get("accountId"));
    }

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

        public void returnNothing() {
        }
    }
}
