package controller;

import com.voidframework.core.bindable.Controller;
import com.voidframework.core.helper.Json;
import com.voidframework.core.helper.VoidFrameworkVersion;
import com.voidframework.web.http.Context;
import com.voidframework.web.http.HttpContentType;
import com.voidframework.web.http.Result;
import com.voidframework.web.http.param.RequestPath;
import com.voidframework.web.http.param.RequestRoute;
import com.voidframework.web.routing.HttpMethod;
import service.HelloWorldService;
import service.MonInterface;

import javax.inject.Inject;
import java.util.Set;

@Controller
public class HelloWorldController implements HttpContentType {

    private final HelloWorldService helloWorldService;

    @Inject
    public HelloWorldController(final HelloWorldService helloWorldService,
                                final Set<MonInterface> monInterfaceSet) {
        this.helloWorldService = helloWorldService;
    }

    @RequestRoute(method = HttpMethod.GET, route = "/")
    //@Cache(key = "sample.say_hello")
    public Result sayHello(final Context context) {
        return Result.ok("""
                <html>
                <head>
                <title>Void Framework</title>
                <meta charset="utf-8">
                <link rel="stylesheet" href="/webjars/bootstrap/5.1.3/css/bootstrap.min.css">
                </head>
                <body>

            <div class="col-lg-8 mx-auto p-3 py-md-5">
                <header class="d-flex align-items-center pb-3 mb-5 border-bottom">
                    <a href="/" class="d-flex align-items-center text-dark text-decoration-none">
                        <img width="32" height="32" class="me-2" role="img" src="/static/favicon.ico"/>
                        <span class="fs-4">Void Framework</span>
                    </a>
                </header>

                <main>
                    <h1>%s</h1>
                </main>
                <footer class="pt-2 my-5 text-muted border-top">
                    Void Framework %s
                </footer>
            </div>
            </body>
            </html>
            """.formatted(helloWorldService.sayHello(), VoidFrameworkVersion.getVersion()));
    }

    @RequestRoute(method = HttpMethod.GET, route = "/(?<name>[0-9]{1,36})")
    //@Cache(key = "{class}::{method}")
    public Result sayHello(final Context context,
                           @RequestPath("name") final int name) {

        return Result.ok("""
            <html>
            <head>
            <title>Void Framework</title>
            <meta charset="utf-8">
            <link rel="stylesheet" href="/webjars/bootstrap/5.1.3/css/bootstrap.min.css">
            </head>
            <body>

            <div class="container">
            <h1>Welcome!</h1>

            <div class="card" style="width: 18rem;">
              <img src="/static/raccoon.jpg" class="card-img-top">
              <div class="card-body">
                <h5 class="card-title">%s</h5>
                <p class="card-text">Your IP is %s.</p>
              </div>
            </div>
            </body>
            </html>
            """.formatted(name, context.getRequest().getRemoteHostName()));
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
