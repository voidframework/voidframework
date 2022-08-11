package dev.voidframework.cache.engine;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.helper.Reflection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class MemoryCacheEngineTest {

    @Test
    public void flushWhenFullMaxItem() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("voidframework.cache.inMemory.flushWhenFullMaxItem = 3");
        final MemoryCacheEngine memoryCacheEngine = new MemoryCacheEngine(configuration);

        // Act
        memoryCacheEngine.set("key", 1337, 60);
        memoryCacheEngine.set("key2", 1337, 60);
        memoryCacheEngine.set("key3", 1337, 60);
        memoryCacheEngine.set("key4", 1337, 60);

        // Assert
        final Map<String, Object> internalCacheMap = Reflection.getFieldValue(memoryCacheEngine, "cacheMap", new Reflection.WrappedClass<>());
        Assertions.assertNotNull(internalCacheMap);
        Assertions.assertEquals(1, internalCacheMap.size());
    }

    @Test
    public void getUnknownValueKey() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("voidframework.cache.inMemory.flushWhenFullMaxItem = 2");
        final MemoryCacheEngine memoryCacheEngine = new MemoryCacheEngine(configuration);

        // Act
        Object value = memoryCacheEngine.get("key");

        // Assert
        Assertions.assertNull(value);
    }

    @Test
    public void getUnknownValueEmpty() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("voidframework.cache.inMemory.flushWhenFullMaxItem = 2");
        final MemoryCacheEngine memoryCacheEngine = new MemoryCacheEngine(configuration);

        // Act
        Object value = memoryCacheEngine.get("");

        // Assert
        Assertions.assertNull(value);

        value = memoryCacheEngine.get(null);
        Assertions.assertNull(value);
    }

    @Test
    public void getUnknownValueNull() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("voidframework.cache.inMemory.flushWhenFullMaxItem = 2");
        final MemoryCacheEngine memoryCacheEngine = new MemoryCacheEngine(configuration);

        // Act
        Object value = memoryCacheEngine.get(null);

        // Assert
        Assertions.assertNull(value);
    }

    @Test
    public void setValueAndGetValueKey() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("voidframework.cache.inMemory.flushWhenFullMaxItem = 2");
        final MemoryCacheEngine memoryCacheEngine = new MemoryCacheEngine(configuration);
        memoryCacheEngine.set("key", 1337, 60);

        // Act
        final Object value = memoryCacheEngine.get("key");

        // Assert
        Assertions.assertEquals(1337, value);

        final Map<String, Object> internalCacheMap = Reflection.getFieldValue(memoryCacheEngine, "cacheMap", new Reflection.WrappedClass<>());
        Assertions.assertNotNull(internalCacheMap);
        Assertions.assertEquals(1, internalCacheMap.size());
    }

    @Test
    public void timeToLive() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("voidframework.cache.inMemory.flushWhenFullMaxItem = 2");
        final MemoryCacheEngine memoryCacheEngine = new MemoryCacheEngine(configuration);

        // Act
        memoryCacheEngine.set("key", 1337, 3600);

        // Assert
        final Map<String, Object> internalCacheMap = Reflection.getFieldValue(memoryCacheEngine, "cacheMap", new Reflection.WrappedClass<>());
        Assertions.assertNotNull(internalCacheMap);
        Assertions.assertEquals(1, internalCacheMap.size());

        LocalDateTime expirationDateTime = Reflection.getFieldValue(internalCacheMap.get("key"), "expirationDate", LocalDateTime.class);
        Assertions.assertNotNull(expirationDateTime);
        Assertions.assertEquals(
            LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES).plusHours(1),
            expirationDateTime.truncatedTo(ChronoUnit.MINUTES));


        memoryCacheEngine.set("key2", 1337, -1);
        Assertions.assertEquals(2, internalCacheMap.size());

        expirationDateTime = Reflection.getFieldValue(internalCacheMap.get("key2"), "expirationDate", LocalDateTime.class);
        Assertions.assertNotNull(expirationDateTime);
        Assertions.assertEquals(LocalDateTime.MAX, expirationDateTime);
    }

    @Test
    public void timeToLiveInfinite() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("voidframework.cache.inMemory.flushWhenFullMaxItem = 2");
        final MemoryCacheEngine memoryCacheEngine = new MemoryCacheEngine(configuration);

        // Act
        memoryCacheEngine.set("key", 1337, -1);

        // Assert
        final Map<String, Object> internalCacheMap = Reflection.getFieldValue(memoryCacheEngine, "cacheMap", new Reflection.WrappedClass<>());
        Assertions.assertNotNull(internalCacheMap);
        Assertions.assertEquals(1, internalCacheMap.size());

        final LocalDateTime expirationDateTime = Reflection.getFieldValue(internalCacheMap.get("key"), "expirationDate", LocalDateTime.class);
        Assertions.assertNotNull(expirationDateTime);
        Assertions.assertEquals(LocalDateTime.MAX, expirationDateTime);
    }

    @Test
    public void timeToLiveExpiration() throws InterruptedException {

        // Arrange
        final Config configuration = ConfigFactory.parseString("voidframework.cache.inMemory.flushWhenFullMaxItem = 2");
        final MemoryCacheEngine memoryCacheEngine = new MemoryCacheEngine(configuration);

        // Act
        memoryCacheEngine.set("key", 1337, 1);
        Thread.sleep(1500);
        final Object value = memoryCacheEngine.get("key");

        // Assert
        Assertions.assertNull(value);

        final Map<String, Object> internalCacheMap = Reflection.getFieldValue(memoryCacheEngine, "cacheMap", new Reflection.WrappedClass<>());
        Assertions.assertNotNull(internalCacheMap);
        Assertions.assertEquals(0, internalCacheMap.size());
    }
}
