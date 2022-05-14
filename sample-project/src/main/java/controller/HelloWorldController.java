package controller;

import com.voidframework.core.http.RequestPath;

import javax.inject.Singleton;

@Singleton
public class HelloWorldController {

    public String sayHello() {
        return "Hello World!";
    }

    public String sayHello(@RequestPath("name") final int name) {
        return "Hello " + name + "!";
    }
}
