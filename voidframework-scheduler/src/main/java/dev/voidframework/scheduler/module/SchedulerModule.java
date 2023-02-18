package dev.voidframework.scheduler.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * The scheduler module.
 *
 * @since 1.0.0
 */
public class SchedulerModule extends AbstractModule {

    @Override
    protected void configure() {

        final ScheduledHandlers schedulerHooks = new ScheduledHandlers();

        bind(ScheduledHandlers.class).toInstance(schedulerHooks);
        bindListener(Matchers.any(), new SchedulerAnnotationListener(schedulerHooks));
    }
}
