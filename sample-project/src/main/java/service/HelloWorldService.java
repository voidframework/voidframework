package service;

import com.voidframework.core.bindable.Service;

@Service
public class HelloWorldService implements MonInterface {

    public String sayHello() {
        return "Hello World!";
    }
}
