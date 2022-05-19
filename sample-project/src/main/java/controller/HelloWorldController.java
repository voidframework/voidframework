package controller;

import com.voidframework.core.helper.Json;
import com.voidframework.core.http.Context;
import com.voidframework.core.http.HttpContentType;
import com.voidframework.core.http.RequestPath;
import com.voidframework.core.http.Result;

import javax.inject.Singleton;

@Singleton
public class HelloWorldController implements HttpContentType {

    public Result sayHello() {
        return Result.ok("Hello World!");
    }

    public Result sayHello(final Context context,
                           @RequestPath("name") final int name) {
        return Result.ok("""
                <h2>Hello %s!</h2>
                <br/>
                Your IP is %s
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
        return Result.ok(context.getRequest().getBodyContent(), APPLICATION_JSON);
    }
}
