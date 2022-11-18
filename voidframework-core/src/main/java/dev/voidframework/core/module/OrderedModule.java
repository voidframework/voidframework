package dev.voidframework.core.module;

/**
 * Allows prioritisation of Guice modules.
 */
public interface OrderedModule {

    /**
     * Returns the priority of the module.
     *
     * @return the priority as integer
     */
    int priority();
}
