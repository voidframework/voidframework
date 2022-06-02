package dev.voidframework.web.module;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import dev.voidframework.web.bindable.WebController;
import dev.voidframework.web.http.param.RequestRoute;
import dev.voidframework.web.routing.Router;

import java.lang.reflect.Method;

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

        if (classType.isAnnotationPresent(WebController.class)) {
            for (final Method method : classType.getMethods()) {
                if (method.isAnnotationPresent(RequestRoute.class)) {
                    final RequestRoute requestRoute = method.getAnnotation(RequestRoute.class);
                    router.addRoute(requestRoute.method(), requestRoute.route(), classType, method);
                }
            }
        }
    }
}
