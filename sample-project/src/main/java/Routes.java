import com.voidframework.web.http.controller.StaticAssetsController;
import com.voidframework.web.routing.AppRoutesDefinition;
import com.voidframework.web.routing.HttpMethod;
import com.voidframework.web.routing.Router;
import controller.HelloWorldController;

/**
 * Defines application routes.
 */
public class Routes implements AppRoutesDefinition {

    @Override
    public void defineAppRoutes(final Router router) {
        router.addRoute(routeBuilder ->
            routeBuilder.method(HttpMethod.GET).route("/").call(HelloWorldController.class, "sayHello"));
        router.addRoute(routeBuilder ->
            routeBuilder.method(HttpMethod.GET).route("/move").call(HelloWorldController.class, "move"));
        router.addRoute(routeBuilder ->
            routeBuilder.method(HttpMethod.GET).route("/json").call(HelloWorldController.class, "sayJson"));
        router.addRoute(routeBuilder ->
            routeBuilder.method(HttpMethod.GET).route("/(?<name>[0-9]{0,36})").call(HelloWorldController.class, "sayHello"));
        router.addRoute(routeBuilder ->
            routeBuilder.method(HttpMethod.POST).route("/form").call(HelloWorldController.class, "postForm"));
        router.addRoute(routeBuilder ->
            routeBuilder.method(HttpMethod.GET).route("/webjars/(?<fileName>.*)").call(StaticAssetsController.class, "webjarAsset"));
        router.addRoute(routeBuilder ->
            routeBuilder.method(HttpMethod.GET).route("/static/(?<fileName>.*)").call(StaticAssetsController.class, "staticAsset"));
        router.addRoute(routeBuilder ->
            routeBuilder.method(HttpMethod.GET).route("/favicon.ico").call(StaticAssetsController.class, "staticAsset"));
    }
}
