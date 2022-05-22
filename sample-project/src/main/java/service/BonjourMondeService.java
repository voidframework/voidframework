package service;

import com.voidframework.core.bindable.Service;

@Service
public class BonjourMondeService implements MonInterface {

    public String sayHello() {
        return "Bonjour le monde!";
    }
}
