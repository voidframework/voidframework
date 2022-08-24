package dev.voidframework.web.module;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import dev.voidframework.web.bindable.WebController;
import dev.voidframework.web.http.annotation.RequestRoute;
import dev.voidframework.web.http.routing.Router;

import java.lang.reflect.Method;

/**
 * Listen to the different bind classes to detect which ones are web controllers.
 */
public class ControllerAnnotationListener implements TypeListener {

    private final Router router;

    /**
     * Build a new instance.
     *
     * @param router The current router
     */
    public ControllerAnnotationListener(final Router router) {

        this.router = router;
    }

    @Override
    public <I> void hear(final TypeLiteral<I> type, final TypeEncounter<I> encounter) {

        final Class<?> classType = type.getRawType();
        final WebController webController = classType.getAnnotation(WebController.class);

        if (webController != null) {
            for (final Method method : classType.getMethods()) {
                if (method.isAnnotationPresent(RequestRoute.class)) {
                    final RequestRoute requestRoute = method.getAnnotation(RequestRoute.class);
                    final String completeRoute = this.appendPrefixToRoute(webController.prefixRoute(), requestRoute.route());

                    router.addRoute(requestRoute.method(), completeRoute, classType, method, requestRoute.name());
                }
            }
        }
    }

    /**
     * Prepends prefix to a route.
     *
     * @param prefix The prefix to prepend
     * @param route  The route to use
     * @return The complete route
     */
    private String appendPrefixToRoute(final String prefix, final String route) {

        final String cleanedPrefix = this.cleanRoutePath(prefix);
        final String cleanedRoute = this.cleanRoutePath(route);

        if (cleanedPrefix.endsWith("/") && cleanedRoute.charAt(0) == '/') {
            return cleanedPrefix + cleanedRoute.substring(1);
        }

        return this.cleanRoutePath(cleanedPrefix + cleanedRoute);
    }

    /**
     * Cleans the given route path.
     *
     * @param routePath The route path to clean
     * @return Cleaned route path
     */
    private String cleanRoutePath(final String routePath) {

        String cleanedRoutePath = routePath.trim();

        if (cleanedRoutePath.isEmpty() || cleanedRoutePath.equals("/")) {
            return "/";
        }

        if (cleanedRoutePath.charAt(0) != '/') {
            cleanedRoutePath = '/' + cleanedRoutePath;
        }
        if (cleanedRoutePath.endsWith("/")) {
            cleanedRoutePath = cleanedRoutePath.substring(0, cleanedRoutePath.length() - 1);
        }

        return cleanedRoutePath;
    }
}
