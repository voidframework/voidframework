package dev.voidframework.core.routing;

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
public class RouterTest {

    @Test
    public void addRoute() {
        final Router router = new DefaultRouter();
        final Method method = resolveMethod("displayHelloWorld", SampleController.class);
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
        final Method methodDisplay = resolveMethod("displayHelloWorld", SampleController.class);
        final Method methodRegister = resolveMethod("displayRegister", SampleController.class);
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
    public void resolveRoute_withRegularExpression() {
        final Router router = new DefaultRouter();
        final Method methodDisplay = resolveMethod("displayHelloWorld", SampleController.class);
        final Method methodRegister = resolveMethod("displayRegister", SampleController.class);
        final Method methodAccount = resolveMethod("displayAccount", SampleController.class);
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

    /**
     * Resolve a methode from it name.
     *
     * @param methodName The method name
     * @param classType  The class where are located this method
     * @return The method
     */
    private Method resolveMethod(final String methodName, final Class<?> classType) {
        for (final Method method : classType.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
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
