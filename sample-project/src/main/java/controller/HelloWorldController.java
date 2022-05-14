package controller;

import javax.inject.Singleton;

@Singleton
public class HelloWorldController {

    public String sayHello() {
        return "Hello World!";
    }

    public String sayHello(final String name) {
        return "Hello " + name + "!";
    }
}
