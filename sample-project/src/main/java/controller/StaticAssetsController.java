package controller;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import dev.voidframework.web.bindable.WebController;
import dev.voidframework.web.http.controller.AbstractStaticAssetsController;

/**
 * Static assets controller.
 */
@WebController
public class StaticAssetsController extends AbstractStaticAssetsController {

    /**
     * Build a new instance;
     *
     * @param configuration The current configuration
     */
    @Inject
    public StaticAssetsController(final Config configuration) {
        super(configuration);
    }
}
