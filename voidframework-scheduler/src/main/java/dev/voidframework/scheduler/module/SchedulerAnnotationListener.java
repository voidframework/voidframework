package dev.voidframework.scheduler.module;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import dev.voidframework.scheduler.Scheduled;

import java.lang.reflect.Method;

/**
 * Listens to the different bind classes to detect which ones have scheduled methods.
 *
 * @since 1.0.0
 */
public class SchedulerAnnotationListener implements TypeListener {

    private final ScheduledHandlers scheduledHandlers;

    /**
     * Build a new instance.
     *
     * @param scheduledHandlers The scheduled hooks
     * @since 1.0.0
     */
    public SchedulerAnnotationListener(final ScheduledHandlers scheduledHandlers) {

        this.scheduledHandlers = scheduledHandlers;
    }

    @Override
    public <I> void hear(final TypeLiteral<I> type, final TypeEncounter<I> encounter) {

        final Class<?> classType = type.getRawType();

        for (final Method method : classType.getDeclaredMethods()) {
            final Scheduled scheduled = method.getAnnotation(Scheduled.class);

            if (scheduled != null) {
                scheduledHandlers.add(new ScheduledHandlers.ScheduledHandler(classType, method, scheduled));
            }
        }
    }
}
