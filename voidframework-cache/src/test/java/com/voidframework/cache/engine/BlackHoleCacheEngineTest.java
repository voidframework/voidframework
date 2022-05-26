package com.voidframework.cache.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public final class BlackHoleCacheEngineTest {

    @Test
    public void getUnknownValue() {
        final BlackHoleCacheEngine blackHoleCacheEngine = new BlackHoleCacheEngine();

        final Object value = blackHoleCacheEngine.get("key");
        Assertions.assertNull(value);
    }

    @Test
    public void setValueAndGetValueKey() {
        final BlackHoleCacheEngine blackHoleCacheEngine = new BlackHoleCacheEngine();

        blackHoleCacheEngine.set("key", 1337, 60);

        final Object value = blackHoleCacheEngine.get("key");
        Assertions.assertNull(value);
    }
}
