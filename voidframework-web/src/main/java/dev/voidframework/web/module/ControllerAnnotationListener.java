package dev.voidframework.web.module;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import dev.voidframework.web.bindable.WebController;
import dev.voidframework.web.http.annotation.RequestRoute;
import dev.voidframework.web.http.routing.RouteURL;
import dev.voidframework.web.http.routing.Router;

import java.lang.reflect.Method;

/**
 * Listen to the different bind classes to detect which ones are web controllers.
 */
public class ControllerAnnotationListener implements TypeListener {

    private final String contextPath;
    private final Router router;

    /**
     * Build a new instance.
     *
     * @param contextPath The context path
     * @param router      The current router
     */
    public ControllerAnnotationListener(final String contextPath, final Router router) {

        this.contextPath = contextPath;
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
                    final RouteURL routeURL = RouteURL.of(this.contextPath, webController.prefixRoute(), requestRoute.route());

                    router.addRoute(requestRoute.method(), routeURL, classType, method, requestRoute.name());
                }
            }
        }
    }
}
