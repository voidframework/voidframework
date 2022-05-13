package com.voidframework.core.routing;

import com.voidframework.core.exception.BadRouteDefinitionException;
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
        BadRouteDefinitionException.Missing thrown;

        thrown = Assertions.assertThrows(BadRouteDefinitionException.Missing.class, () -> {
            final Router router = new DefaultRouter();
            router.addRoute(routeBuilder -> routeBuilder.route("/").call(SampleController.class, "displayHelloWorld"));
        });
        Assertions.assertEquals("Value 'method' is missing", thrown.getMessage());

        thrown = Assertions.assertThrows(BadRouteDefinitionException.Missing.class, () -> {
            final Router router = new DefaultRouter();
            router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).call(SampleController.class, "displayHelloWorld"));
        });
        Assertions.assertEquals("Value 'route' is missing", thrown.getMessage());

        thrown = Assertions.assertThrows(BadRouteDefinitionException.Missing.class, () -> {
            final Router router = new DefaultRouter();
            router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/"));
        });
        Assertions.assertEquals("Value 'controllerClass' is missing", thrown.getMessage());

        thrown = Assertions.assertThrows(BadRouteDefinitionException.Missing.class, () -> {
            final Router router = new DefaultRouter();
            router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/").call(SampleController.class, null));
        });
        Assertions.assertEquals("Value 'methodName' is missing", thrown.getMessage());
    }

    @Test
    public void addRoute_badRoutePattern() {
        final BadRouteDefinitionException.BadValue thrown = Assertions.assertThrows(BadRouteDefinitionException.BadValue.class, () -> {
            final Router router = new DefaultRouter();
            router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("[-").call(SampleController.class, "displayHelloWorld"));
        });
        Assertions.assertEquals("Invalid value 'route': Can't compile regular expression", thrown.getMessage());
    }

    @Test
    public void addRoute_controllerMethodDoesNotExists() {
        final BadRouteDefinitionException.ControllerMethodDoesNotExists thrown = Assertions.assertThrows(
            BadRouteDefinitionException.ControllerMethodDoesNotExists.class,
            () -> {
                final Router router = new DefaultRouter();
                router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/").call(SampleController.class, "unknownMethodName"));
            });
        Assertions.assertEquals("Method 'com.voidframework.core.routing.RouterTest$SampleController::unknownMethodName' does not exists", thrown.getMessage());
    }

    @Test
    public void addRoute_controllerMethodDoesNotReturnsValue() {
        final BadRouteDefinitionException.ControllerMethodDoesNotReturnsValue thrown = Assertions.assertThrows(
            BadRouteDefinitionException.ControllerMethodDoesNotReturnsValue.class,
            () -> {
                final Router router = new DefaultRouter();
                router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/").call(SampleController.class, "returnNothing"));
            });
        Assertions.assertEquals("Method 'com.voidframework.core.routing.RouterTest$SampleController::returnNothing' does not returns value", thrown.getMessage());
    }

    @Test
    public void resolveRoute() {
        final Router router = new DefaultRouter();
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/").call(SampleController.class, "displayHelloWorld"));
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/register").call(SampleController.class, "displayRegister"));

        final Route route = router.resolveRoute(HttpMethod.GET, "/register").orElse(null);
        Assertions.assertNotNull(route);
        Assertions.assertEquals(HttpMethod.GET, route.httpMethod);
        Assertions.assertEquals("/register", route.routePattern.pattern());
        Assertions.assertEquals(SampleController.class, route.controllerClass);
        Assertions.assertEquals("displayRegister", route.method.getName());
    }

    @Test
    public void resolveRoute_withRegularExpression() {
        final Router router = new DefaultRouter();
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/").call(SampleController.class, "displayHelloWorld"));
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/register").call(SampleController.class, "displayRegister"));
        router.addRoute(routeBuilder -> routeBuilder.method(HttpMethod.GET).route("/register/(?<accountId>[a-z]{0,36})").call(SampleController.class, "displayAccount"));

        final Route route = router.resolveRoute(HttpMethod.GET, "/register/toto").orElse(null);
        Assertions.assertNotNull(route);
        Assertions.assertEquals(HttpMethod.GET, route.httpMethod);
        Assertions.assertEquals("/register/(?<accountId>[a-z]{0,36})", route.routePattern.pattern());
        Assertions.assertEquals(SampleController.class, route.controllerClass);
        Assertions.assertEquals("displayAccount", route.method.getName());
    }

    private static final class SampleController {

        public String displayHelloWorld() {
            return "Hello World!";
        }

        public String displayRegister() {
            return "Register Form";
        }

        public String displayAccount(final String accountId) {
            return "My Account";
        }

        public void returnNothing() {
        }
    }
}
