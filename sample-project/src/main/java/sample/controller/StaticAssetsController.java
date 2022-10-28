package sample.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.web.bindable.WebController;
import dev.voidframework.web.http.controller.AbstractStaticAssetsController;

/**
 * Static assets controller.
 */
@Singleton
@WebController
public final class StaticAssetsController extends AbstractStaticAssetsController {

    /**
     * Build a new instance;
     *
     * @param configuration The application configuration
     */
    @Inject
    public StaticAssetsController(final Config configuration) {
        super(configuration);
    }
}
