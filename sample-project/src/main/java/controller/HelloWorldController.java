package controller;

import com.google.inject.Inject;
import dev.voidframework.core.helper.Json;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.web.bindable.WebController;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.HttpContentType;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.param.RequestPath;
import dev.voidframework.web.http.param.RequestRoute;
import dev.voidframework.web.routing.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@WebController
public class HelloWorldController implements HttpContentType {

    private final TemplateRenderer templateRenderer;

    @Inject
    public HelloWorldController(final TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    @RequestRoute(method = HttpMethod.GET, route = "/")
    public Result showHomePage(final Context context) {
        return Result.ok(this.templateRenderer.render("home_page.ftl", context.getLocale()));
    }

    @RequestRoute(method = HttpMethod.GET, route = "/(?<name>[0-9]{1,36})")
    public Result sayHello(final Context context,
                           @RequestPath("name") final int name) {

        final Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("name", name);
        dataModel.put("remoteHostName", context.getRequest().getRemoteHostName());

        return Result.ok(this.templateRenderer.render("say_hello.ftl", context.getLocale(), dataModel));
    }

    @RequestRoute(method = HttpMethod.GET, route = "/json")
    public Result sayJson(final Context context) {

        return Result.ok(Json.toJson(context.getRequest().getHeaders()));
    }

    @RequestRoute(method = HttpMethod.GET, route = "/move")
    public Result move(final Context context) {

        return Result.redirectTemporaryTo("/json");
    }

    @RequestRoute(method = HttpMethod.POST, route = "/form")
    public Result postForm(final Context context) {

        return Result.ok(context.getRequest().getBodyContent().asRaw(), APPLICATION_JSON);
        //return Result.ok(context.getRequest().getBodyContent().asFormData().get("").get(0).inputStream(), IMAGE_JPEG);
    }
}
