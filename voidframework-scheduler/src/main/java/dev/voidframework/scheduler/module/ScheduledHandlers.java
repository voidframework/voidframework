package dev.voidframework.scheduler.module;

import dev.voidframework.scheduler.Scheduled;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * A collection of scheduled method handler.
 *
 * @since 1.0.0
 */
public class ScheduledHandlers extends ArrayList<ScheduledHandlers.ScheduledHandler> {

    /**
     * A single scheduled method handler.
     *
     * @param classType           The class type
     * @param method              The method from the class
     * @param scheduledAnnotation The scheduled annotation
     * @since 1.0.0
     */
    public record ScheduledHandler(Class<?> classType,
                                   Method method,
                                   Scheduled scheduledAnnotation) {
    }
}
