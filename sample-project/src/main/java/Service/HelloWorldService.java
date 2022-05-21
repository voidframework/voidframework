package Service;

import com.google.inject.Singleton;

@Singleton
public class HelloWorldService {

    public String sayHello() {
        return "Hello World!";
    }
}
