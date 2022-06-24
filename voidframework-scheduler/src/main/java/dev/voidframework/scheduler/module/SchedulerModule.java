package dev.voidframework.scheduler.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import dev.voidframework.scheduler.SchedulerManager;

/**
 * The scheduler module.
 */
public class SchedulerModule extends AbstractModule {

    @Override
    protected void configure() {
        final ScheduledHandlers schedulerHooks = new ScheduledHandlers();

        bind(ScheduledHandlers.class).toInstance(schedulerHooks);
        bind(SchedulerManager.class).asEagerSingleton();
        bindListener(Matchers.any(), new SchedulerAnnotationListener(schedulerHooks));
    }
}
