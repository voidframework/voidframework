package controller;

import com.voidframework.core.helper.Json;
import com.voidframework.web.http.Context;
import com.voidframework.web.http.HttpContentType;
import com.voidframework.web.http.Result;
import com.voidframework.web.http.param.RequestPath;

import javax.inject.Singleton;

@Singleton
public class HelloWorldController implements HttpContentType {

    public Result sayHello() {
        return Result.ok("Hello World!");
    }

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
                """.formatted(name, context.getRequest().getRemoteHostName()))
            .setHeader("TOTO", "VALUE");
    }

    public Result sayJson(final Context context) {
        return Result.ok(Json.toJson(context.getRequest().getHeaders()));
    }

    public Result move(final Context context) {
        return Result.redirectTemporaryTo("/json");
    }

    public Result postForm(final Context context) {
        return Result.ok(context.getRequest().getBodyContent().asRaw(), APPLICATION_JSON);
        //return Result.ok(context.getRequest().getBodyContent().asFormData().get("").get(0).inputStream(), IMAGE_JPEG);
    }
}
