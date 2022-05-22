package com.voidframework.web.module;

import com.google.inject.spi.InjectionListener;
import com.voidframework.core.helper.ProxyDetector;
import com.voidframework.web.http.param.RequestRoute;
import com.voidframework.web.routing.Router;

import java.lang.reflect.Method;

/**
 * Listen for injections into instances.
 *
 * @param <INSTANCE_TYPE> Injected instance type
 */
public class ControllerInjectionListener<INSTANCE_TYPE> implements InjectionListener<INSTANCE_TYPE> {

    private final Router router;

    /**
     * Build a new instance.
     *
     * @param router The current router
     */
    public ControllerInjectionListener(final Router router) {
        this.router = router;
    }

    @Override
    public void afterInjection(final INSTANCE_TYPE injectee) {
        final Class<?> controllerClass = ProxyDetector.isProxy(injectee)
            ? injectee.getClass().getSuperclass()
            : injectee.getClass();

        for (final Method method : controllerClass.getMethods()) {
            if (method.isAnnotationPresent(RequestRoute.class)) {
                final RequestRoute requestRoute = method.getAnnotation(RequestRoute.class);
                router.addRoute(requestRoute.method(), requestRoute.route(), injectee, method);
            }
        }
    }
}
