package dev.voidframework.cache.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
final class BlackHoleCacheEngineTest {

    @Test
    void getUnknownValue() {

        // Arrange
        final BlackHoleCacheEngine blackHoleCacheEngine = new BlackHoleCacheEngine();

        // Act
        final Object value = blackHoleCacheEngine.get("key");

        // Assert
        Assertions.assertNull(value);
    }

    @Test
    void setValueAndGetValueKey() {

        // Arrange
        final BlackHoleCacheEngine blackHoleCacheEngine = new BlackHoleCacheEngine();
        blackHoleCacheEngine.set("key", 1337, 60);

        // Act
        final Object value = blackHoleCacheEngine.get("key");

        // Assert
        Assertions.assertNull(value);
    }

    @Test
    void remove() {

        // Arrange
        final BlackHoleCacheEngine blackHoleCacheEngine = new BlackHoleCacheEngine();

        // Act
        blackHoleCacheEngine.remove("key");

        // Assert -- Not needed here
        Assertions.assertTrue(true);
    }
}
