package dev.voidframework.web.http.routing;

/**
 * Adds a post initialization callback to the router.
 *
 * @since 1.0.1
 */
public interface RouterPostInitialization {

    /**
     * This method will be called after all routes has been discovered and registered.
     *
     * @since 1.0.1
     */
    void onPostInitialization();
}
