package dev.voidframework.core.routing;

import dev.voidframework.core.helper.Reflection;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.param.RequestPath;
import dev.voidframework.web.routing.HttpMethod;
import dev.voidframework.web.routing.ResolvedRoute;
import dev.voidframework.web.routing.Route;
import dev.voidframework.web.routing.Router;
import dev.voidframework.web.routing.impl.DefaultRouter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.lang.reflect.Method;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class RouterTest {

    @Test
    public void addRoute() {

        final Router router = new DefaultRouter();
        final Method method = Reflection.resolveMethod("displayHelloWorld", SampleController.class);
        router.addRoute(HttpMethod.GET, "/", SampleController.class, method);

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
    public void resolveRoute() {

        final Router router = new DefaultRouter();
        final Method methodDisplay = Reflection.resolveMethod("displayHelloWorld", SampleController.class);
        final Method methodRegister = Reflection.resolveMethod("displayRegister", SampleController.class);
        router.addRoute(HttpMethod.GET, "/", SampleController.class, methodDisplay);
        router.addRoute(HttpMethod.GET, "/register", SampleController.class, methodRegister);

        final ResolvedRoute resolvedRoute = router.resolveRoute(HttpMethod.GET, "/register");
        Assertions.assertNotNull(resolvedRoute);
        Assertions.assertEquals(SampleController.class, resolvedRoute.controllerClassType());
        Assertions.assertEquals("displayRegister", resolvedRoute.method().getName());
        Assertions.assertNotNull(resolvedRoute.extractedParameterValues());
        Assertions.assertEquals(0, resolvedRoute.extractedParameterValues().size());
    }

    @Test
    public void resolveRouteWithRegularExpression() {

        final Router router = new DefaultRouter();
        final Method methodDisplay = Reflection.resolveMethod("displayHelloWorld", SampleController.class);
        final Method methodRegister = Reflection.resolveMethod("displayRegister", SampleController.class);
        final Method methodAccount = Reflection.resolveMethod("displayAccount", SampleController.class);
        router.addRoute(HttpMethod.GET, "/", SampleController.class, methodDisplay);
        router.addRoute(HttpMethod.GET, "/register", SampleController.class, methodRegister);
        router.addRoute(HttpMethod.GET, "/register/(?<accountId>[a-z]{0,36})", SampleController.class, methodAccount);

        final ResolvedRoute resolvedRoute = router.resolveRoute(HttpMethod.GET, "/register/toto");
        Assertions.assertNotNull(resolvedRoute);
        Assertions.assertEquals(SampleController.class, resolvedRoute.controllerClassType());
        Assertions.assertEquals("displayAccount", resolvedRoute.method().getName());
        Assertions.assertNotNull(resolvedRoute.extractedParameterValues());
        Assertions.assertEquals(1, resolvedRoute.extractedParameterValues().size());
        Assertions.assertTrue(resolvedRoute.extractedParameterValues().containsKey("accountId"));
        Assertions.assertEquals("toto", resolvedRoute.extractedParameterValues().get("accountId"));
    }

    @Test
    public void reverseUrlWithName() {

        final Router router = new DefaultRouter();
        final Method methodAccount = Reflection.resolveMethod("displayAccount", SampleController.class);
        router.addRoute(HttpMethod.GET, "/account/(?<accountId>[a-z0-9\\-]{36})", SampleController.class, methodAccount, "account");

        final String url = router.reverseRoute("account", List.of("33d7ed6b-9034-4305-8f51-950914f9b08f"));
        Assertions.assertNotNull(url);
        Assertions.assertEquals("/account/33d7ed6b-9034-4305-8f51-950914f9b08f", url);
    }

    @Test
    public void reverseUrlWithoutName() {

        final Router router = new DefaultRouter();
        final Method methodAccount = Reflection.resolveMethod("displayAccount", SampleController.class);
        router.addRoute(HttpMethod.GET, "/account/(?<accountId>[a-z0-9\\-]{36})", SampleController.class, methodAccount);

        final String url = router.reverseRoute(
            "dev.voidframework.core.routing.RouterTest.SampleController.displayAccount",
            List.of("33d7ed6b-9034-4305-8f51-950914f9b08f"));
        Assertions.assertNotNull(url);
        Assertions.assertEquals("/account/33d7ed6b-9034-4305-8f51-950914f9b08f", url);
    }

    @Test
    public void reverseUrlBase() {

        final Router router = new DefaultRouter();
        final Method methodHelloWorld = Reflection.resolveMethod("displayHelloWorld", SampleController.class);
        router.addRoute(HttpMethod.GET, "/", SampleController.class, methodHelloWorld, "helloworld");

        final String url = router.reverseRoute("helloworld", List.of());
        Assertions.assertNotNull(url);
        Assertions.assertEquals("/", url);
    }

    @Test
    public void reverseUrlNoArguments() {

        final Router router = new DefaultRouter();
        final Method methodHelloWorld = Reflection.resolveMethod("displayHelloWorld", SampleController.class);
        router.addRoute(HttpMethod.GET, "/test", SampleController.class, methodHelloWorld, "helloworld");

        final String url = router.reverseRoute("helloworld", List.of());
        Assertions.assertNotNull(url);
        Assertions.assertEquals("/test", url);
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
