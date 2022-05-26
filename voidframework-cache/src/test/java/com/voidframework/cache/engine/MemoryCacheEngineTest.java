package com.voidframework.cache.engine;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
public final class MemoryCacheEngineTest {

    @Test
    @SuppressWarnings("unchecked")
    public void flushWhenFullMaxItem() {
        final Config configuration = ConfigFactory.parseString("voidframework.cache.inMemory.flushWhenFullMaxItem=3");
        final MemoryCacheEngine memoryCacheEngine = new MemoryCacheEngine(configuration);

        final Map<String, Object> internalCacheMap = (Map<String, Object>) getPrivateFieldValue(memoryCacheEngine, "cacheMap");
        Assertions.assertNotNull(internalCacheMap);
        Assertions.assertEquals(0, internalCacheMap.size());

        memoryCacheEngine.set("key", 1337, 60);
        Assertions.assertEquals(1, internalCacheMap.size());

        memoryCacheEngine.set("key2", 1337, 60);
        Assertions.assertEquals(2, internalCacheMap.size());

        memoryCacheEngine.set("key3", 1337, 60);
        Assertions.assertEquals(3, internalCacheMap.size());

        memoryCacheEngine.set("key4", 1337, 60);
        Assertions.assertEquals(1, internalCacheMap.size());
    }

    @Test
    public void getUnknownValue() {
        final Config configuration = ConfigFactory.parseString("voidframework.cache.inMemory.flushWhenFullMaxItem=2");
        final MemoryCacheEngine memoryCacheEngine = new MemoryCacheEngine(configuration);

        Object value = memoryCacheEngine.get("key");
        Assertions.assertNull(value);

        value = memoryCacheEngine.get("");
        Assertions.assertNull(value);

        value = memoryCacheEngine.get(null);
        Assertions.assertNull(value);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void setValueAndGetValueKey() {
        final Config configuration = ConfigFactory.parseString("voidframework.cache.inMemory.flushWhenFullMaxItem=2");
        final MemoryCacheEngine memoryCacheEngine = new MemoryCacheEngine(configuration);

        final Map<String, Object> internalCacheMap = (Map<String, Object>) getPrivateFieldValue(memoryCacheEngine, "cacheMap");
        Assertions.assertNotNull(internalCacheMap);
        Assertions.assertEquals(0, internalCacheMap.size());

        final Integer flushWhenFullMaxItem = (Integer) getPrivateFieldValue(memoryCacheEngine, "flushWhenFullMaxItem");
        Assertions.assertNotNull(flushWhenFullMaxItem);
        Assertions.assertEquals(2, flushWhenFullMaxItem);

        memoryCacheEngine.set("key", 1337, 60);
        Assertions.assertEquals(1, internalCacheMap.size());

        final Object value = memoryCacheEngine.get("key");
        Assertions.assertNotNull(value);
        Assertions.assertEquals(1337, value);

        Assertions.assertEquals(1, internalCacheMap.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void timeToLive() {
        final Config configuration = ConfigFactory.parseString("voidframework.cache.inMemory.flushWhenFullMaxItem=2");
        final MemoryCacheEngine memoryCacheEngine = new MemoryCacheEngine(configuration);

        final Map<String, Object> internalCacheMap = (Map<String, Object>) getPrivateFieldValue(memoryCacheEngine, "cacheMap");
        Assertions.assertNotNull(internalCacheMap);
        Assertions.assertEquals(0, internalCacheMap.size());

        memoryCacheEngine.set("key", 1337, 3600);
        Assertions.assertEquals(1, internalCacheMap.size());

        LocalDateTime expirationDateTime = (LocalDateTime) getPrivateFieldValue(internalCacheMap.get("key"), "expirationDate");
        Assertions.assertNotNull(expirationDateTime);
        Assertions.assertEquals(
            LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES).plusHours(1),
            expirationDateTime.truncatedTo(ChronoUnit.MINUTES));


        memoryCacheEngine.set("key2", 1337, -1);
        Assertions.assertEquals(2, internalCacheMap.size());

        expirationDateTime = (LocalDateTime) getPrivateFieldValue(internalCacheMap.get("key2"), "expirationDate");
        Assertions.assertNotNull(expirationDateTime);
        Assertions.assertEquals(LocalDateTime.MAX, expirationDateTime);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void timeToLiveExpiration() throws InterruptedException {
        final Config configuration = ConfigFactory.parseString("voidframework.cache.inMemory.flushWhenFullMaxItem=2");
        final MemoryCacheEngine memoryCacheEngine = new MemoryCacheEngine(configuration);

        final Map<String, Object> internalCacheMap = (Map<String, Object>) getPrivateFieldValue(memoryCacheEngine, "cacheMap");
        Assertions.assertNotNull(internalCacheMap);
        Assertions.assertEquals(0, internalCacheMap.size());

        final Integer flushWhenFullMaxItem = (Integer) getPrivateFieldValue(memoryCacheEngine, "flushWhenFullMaxItem");
        Assertions.assertNotNull(flushWhenFullMaxItem);
        Assertions.assertEquals(2, flushWhenFullMaxItem);

        memoryCacheEngine.set("key", 1337, 1);
        Assertions.assertEquals(1, internalCacheMap.size());

        Thread.sleep(1500);
        Assertions.assertEquals(1, internalCacheMap.size());

        final Object value = memoryCacheEngine.get("key");
        Assertions.assertNull(value);

        Assertions.assertEquals(0, internalCacheMap.size());
    }

    /**
     * Retrieves the value of a private field.
     *
     * @param classInstance The instance of the class in which the field is located
     * @param fieldName     The field name
     * @return The field value, otherwise, null
     */
    public Object getPrivateFieldValue(final Object classInstance, final String fieldName) {
        try {
            final Field field = classInstance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(classInstance);
        } catch (final Exception ignore) {
            return null;
        }
    }
}
