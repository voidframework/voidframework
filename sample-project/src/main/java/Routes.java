import com.voidframework.core.routing.AppRoutesDefinition;
import com.voidframework.core.routing.HttpMethod;
import com.voidframework.core.routing.Router;
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
            routeBuilder.method(HttpMethod.GET).route("/(?<name>[0-9]{0,36})").call(HelloWorldController.class, "sayHello"));
    }
}
