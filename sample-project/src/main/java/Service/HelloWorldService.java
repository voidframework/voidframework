package service;

import com.voidframework.core.bindable.Service;

@Service
public class HelloWorldService {

    public String sayHello() {
        return "Hello World!";
    }
}
