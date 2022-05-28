package com.voidframework.healthcheck.checker;

import com.voidframework.healthcheck.Health;
import com.voidframework.healthcheck.HealthChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public final class JavaVirtualMachineHealthCheckerTest {

    @Test
    public void checkHealth() {
        final HealthChecker healthChecker = new JavaVirtualMachineHealthChecker();
        Assertions.assertEquals("JVM", healthChecker.getName());

        final Health health = healthChecker.checkHealth();
        Assertions.assertNotNull(health);

        Assertions.assertEquals(Health.Status.UP, health.status());
        Assertions.assertNotNull(health.details());
        Assertions.assertEquals(4, health.details().size());

        Assertions.assertEquals(Runtime.version().toString(), health.details().get("javaVersion"));
        Assertions.assertTrue(health.details().get("maxMemory") instanceof Long);
        Assertions.assertTrue(health.details().get("totalMemory") instanceof Long);
        Assertions.assertTrue(health.details().get("freeMemory") instanceof Long);
        Assertions.assertTrue((Long) health.details().get("maxMemory") > 0L);
        Assertions.assertTrue((Long) health.details().get("totalMemory") > 0L);
        Assertions.assertTrue((Long) health.details().get("freeMemory") > 0L);
    }
}
