package dev.voidframework.web.http.routing;

/**
 * This interface allows application to manually define http routes.
 *
 * @since 1.0.0
 */
public interface AppRoutesDefinition {

    /**
     * Defines applications routes.
     *
     * @param router Instance of the Router
     * @since 1.0.0
     */
    void defineAppRoutes(final Router router);
}
