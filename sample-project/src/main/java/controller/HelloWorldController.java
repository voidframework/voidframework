package controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import dev.voidframework.core.helper.Json;
import dev.voidframework.core.helper.Yaml;
import dev.voidframework.template.TemplateRenderer;
import dev.voidframework.web.bindable.WebController;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.HttpContentType;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.param.RequestPath;
import dev.voidframework.web.http.param.RequestRoute;
import dev.voidframework.web.routing.HttpMethod;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        final Pojo pojo = context.getRequest().getBodyContent().asJson(Pojo.class);
        final Pojo pojo2 = context.getRequest().getBodyContent().asYaml(Pojo.class);
        return Result.ok(Yaml.toYaml(pojo == null ? pojo2 : pojo).getBytes(StandardCharsets.UTF_8), TEXT_YAML);
    }

    public static class Pojo {

        public final String id = UUID.randomUUID().toString();

        public final String firstName;
        public final String lastName;

        @JsonCreator
        public Pojo(@JsonProperty("firstName") final String firstName,
                    @JsonProperty("lastName") final String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}
