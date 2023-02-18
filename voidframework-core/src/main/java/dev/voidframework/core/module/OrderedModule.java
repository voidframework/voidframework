package dev.voidframework.core.module;

/**
 * Allows prioritisation of Guice modules.
 *
 * @since 1.4.0
 */
public interface OrderedModule {

    /**
     * Returns the priority of the module.
     *
     * @return the priority as integer
     * @since 1.4.0
     */
    int priority();
}
