package dev.voidframework.web.http.routing;

/**
 * Adds a post initialization callback to the router.
 */
public interface RouterPostInitialization {

    /**
     * This method will be called after all routes has been discovered and registered.
     */
    void onPostInitialization();
}
