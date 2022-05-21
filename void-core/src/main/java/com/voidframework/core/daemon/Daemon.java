package com.voidframework.core.daemon;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.typesafe.config.Config;

/**
 * A Daemon is process running in the background.
 */
public interface Daemon extends Runnable {

    /**
     * Retrieve the start/stop priority.
     *
     * @return The priority
     */
    int getPriority();

    /**
     * Retrieve the time (in milliseconds) for the daemon to shut down properly
     *
     * @return the time in milliseconds
     */
    long getGracefulStopTimeout();

    /**
     * Retrieve module to register (OPTIONAL)
     *
     * @return The module to register
     */
    Module getModule();

    /**
     * Configure the daemon.
     *
     * @param configuration The configuration
     * @param injector      The injector
     */
    void configure(final Config configuration, final Injector injector);

    /**
     * Try to gracefully stop the daemon.
     */
    void gracefulStop();
}
