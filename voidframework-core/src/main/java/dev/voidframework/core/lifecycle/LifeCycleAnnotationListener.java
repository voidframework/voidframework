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
    public <I> void hear(final TypeLiteral<I> type, final TypeEncounter<I> encounter) {

        final Class<?> classType = type.getRawType();

        for (final Method method : classType.getMethods()) {
            final LifeCycleStart lifeCycleStartAnnotation = method.getAnnotation(LifeCycleStart.class);
            if (lifeCycleStartAnnotation != null) {
                this.lifeCycleManager.registerStart(classType, method, lifeCycleStartAnnotation.priority());
            }

            final LifeCycleStop lifeCycleStopAnnotation = method.getAnnotation(LifeCycleStop.class);
            if (lifeCycleStopAnnotation != null) {
                this.lifeCycleManager.registerStop(
                    classType, method, lifeCycleStopAnnotation.priority(), lifeCycleStopAnnotation.gracefulStopTimeoutConfigKey());
            }
        }
    }
}
