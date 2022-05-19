package com.voidframework.core.daemon;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.typesafe.config.Config;

/**
 * A Daemon is process running in the background.
 */
public interface Daemon extends Runnable {

    int getPriority();

    long getGracefulStopTimeout();

    boolean isRunning();

    Module getModule();

    void configure(final Config configuration, final Injector injector);

    void gracefulStop();
}
