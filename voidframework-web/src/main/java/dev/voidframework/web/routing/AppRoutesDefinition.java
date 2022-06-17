package dev.voidframework.web.routing;

/**
 * This interface allows application to manually define http routes.
 */
public interface AppRoutesDefinition {

    /**
     * Defines applications routes.
     *
     * @param router Instance of the Router
     */
    void defineAppRoutes(final Router router);
}
