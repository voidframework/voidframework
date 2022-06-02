package dev.voidframework.cache.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class BlackHoleCacheEngineTest {

    @Test
    public void getUnknownValue() {
        final dev.voidframework.cache.engine.BlackHoleCacheEngine blackHoleCacheEngine = new dev.voidframework.cache.engine.BlackHoleCacheEngine();

        final Object value = blackHoleCacheEngine.get("key");
        Assertions.assertNull(value);
    }

    @Test
    public void setValueAndGetValueKey() {
        final dev.voidframework.cache.engine.BlackHoleCacheEngine blackHoleCacheEngine = new dev.voidframework.cache.engine.BlackHoleCacheEngine();

        blackHoleCacheEngine.set("key", 1337, 60);

        final Object value = blackHoleCacheEngine.get("key");
        Assertions.assertNull(value);
    }
}
