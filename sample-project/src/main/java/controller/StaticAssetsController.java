package controller;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import com.voidframework.core.bindable.Controller;
import com.voidframework.web.http.controller.AbstractStaticAssetsController;

@Controller
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
