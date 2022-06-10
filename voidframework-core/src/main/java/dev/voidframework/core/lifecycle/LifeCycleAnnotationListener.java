package dev.voidframework.core.lifecycle;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Method;

/**
 * Listens to the different bind classes to detect which ones have
 * methods to call when starting or stopping the application.
 */
public final class LifeCycleAnnotationListener implements TypeListener {

    private final LifeCycleManager lifeCycleManager;

    /**
     * Build a new instance.
     *
     * @param lifeCycleManager The life cycle manager to use
     */
    public LifeCycleAnnotationListener(final LifeCycleManager lifeCycleManager) {
        this.lifeCycleManager = lifeCycleManager;
    }

    @Override
    public <CLASS_TYPE> void hear(final TypeLiteral<CLASS_TYPE> type, final TypeEncounter<CLASS_TYPE> encounter) {
        final Class<?> classType = type.getRawType();

        for (final Method method : classType.getMethods()) {
            if (method.isAnnotationPresent(LifeCycleStart.class) || method.isAnnotationPresent(LifeCycleStop.class)) {
                encounter.register(new LifeCycleInjectionListener<>(lifeCycleManager));
                break;
            }
        }
    }
}