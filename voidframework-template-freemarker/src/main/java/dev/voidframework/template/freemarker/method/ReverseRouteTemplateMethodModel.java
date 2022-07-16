package dev.voidframework.template.freemarker.method;

import dev.voidframework.web.routing.Router;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.util.ArrayList;
import java.util.List;

/**
 * FreeMarker method: reverse route (Web/Router).
 */
public class ReverseRouteTemplateMethodModel implements TemplateMethodModelEx {

    private final Router router;

    /**
     * Build a new instance.
     *
     * @param router The router
     */
    public ReverseRouteTemplateMethodModel(final Router router) {
        this.router = router;
    }

    @Override
    public Object exec(final List argumentList) throws TemplateModelException {

        if (argumentList.size() < 1) {
            throw new TemplateModelException("Wrong arguments");
        }

        final List<Object> parsedArgumentList = new ArrayList<>();
        for (final Object argument : argumentList) {
            if (argument instanceof SimpleScalar) {
                parsedArgumentList.add(((SimpleScalar) argument).getAsString());
            } else if (argument instanceof SimpleNumber) {
                parsedArgumentList.add(((SimpleNumber) argument).getAsNumber());
            } else if (argument instanceof SimpleDate) {
                parsedArgumentList.add(((SimpleDate) argument).getAsDate());
            }
        }

        return router.reverseRoute(
            (String) parsedArgumentList.get(0),
            parsedArgumentList.subList(1, parsedArgumentList.size()));
    }
}
