package com.voidframework.web.module;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.voidframework.core.bindable.Controller;
import com.voidframework.web.http.param.RequestRoute;
import com.voidframework.web.routing.Router;

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

        if (classType.isAnnotationPresent(Controller.class)) {
            for (final Method method : classType.getMethods()) {
                if (method.isAnnotationPresent(RequestRoute.class)) {
                    final RequestRoute requestRoute = method.getAnnotation(RequestRoute.class);
                    router.addRoute(requestRoute.method(), requestRoute.route(), classType, method);
                }
            }
        }
    }
}
