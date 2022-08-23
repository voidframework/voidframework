package sample.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.voidframework.core.helper.Json;
import dev.voidframework.core.helper.Yaml;
import dev.voidframework.validation.Validated;
import dev.voidframework.validation.Validation;
import dev.voidframework.web.bindable.WebController;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.HttpContentType;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.TemplateResult;
import dev.voidframework.web.http.param.RequestBody;
import dev.voidframework.web.http.param.RequestPath;
import dev.voidframework.web.http.param.RequestRoute;
import dev.voidframework.web.routing.HttpMethod;
import sample.entity.Pojo;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple "Hello World" web controller.
 */
@Singleton
@WebController
public class HelloWorldController {

    private final Validation validation;

    /**
     * Build a new instance.
     *
     * @param validation The validation service instance
     */
    @Inject
    public HelloWorldController(final Validation validation) {
        this.validation = validation;
    }

    /**
     * Display the home page.
     *
     * @return A Result
     */
    @RequestRoute(method = HttpMethod.GET)
    public Result showHomePage() {
        return Result.ok(TemplateResult.of("home_page.ftl"));
    }

    /**
     * Display another page.
     *
     * @param context The current context
     * @param number  A number
     * @return A Result
     */
    @RequestRoute(method = HttpMethod.GET, route = "/(?<number>[0-9]{1,36})")
    public Result sayHello(final Context context,
                           @RequestPath("number") final int number) {

        final Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("name", number);
        dataModel.put("remoteHostName", context.getRequest().getRemoteHostName());

        return Result.ok(TemplateResult.of("say_hello.ftl", dataModel));
    }

    /**
     * Retrieves the HTTP request headers in JSON.
     *
     * @param context The current context
     * @return A Result
     */
    @RequestRoute(method = HttpMethod.GET, route = "/json")
    public Result headersAsJson(final Context context) {

        return Result.ok(Json.toJson(context.getRequest().getHeaders()));
    }

    /**
     * Demo HTTP form.
     *
     * @param pojo The POJO retrieved from the body content
     * @return A Result
     */
    @RequestRoute(method = HttpMethod.POST, route = "/form")
    public Result postForm(@RequestBody final Pojo pojo) {
        return Result.ok(Yaml.toString(pojo).getBytes(StandardCharsets.UTF_8), HttpContentType.TEXT_YAML);
    }

    /**
     * Demo HTTP form.
     *
     * @param context The current context
     * @param pojo    The POJO retrieved from the body content
     * @return A Result
     */
    @RequestRoute(method = HttpMethod.POST, route = "/form2")
    public Result postForm2(final Context context, @RequestBody Pojo pojo) {
        final Validated<Pojo> pojoValidated = this.validation.validate(pojo, context.getLocale());
        if (pojoValidated.hasError()) {
            return Result.badRequest(Yaml.toString(pojoValidated.getError()).getBytes(StandardCharsets.UTF_8), HttpContentType.TEXT_YAML);
        }

        return Result.ok(Yaml.toString(pojoValidated.getInstance()).getBytes(StandardCharsets.UTF_8), HttpContentType.TEXT_YAML);
    }
}
