package dev.voidframework.healthcheck.checker;

import dev.voidframework.healthcheck.Health;
import dev.voidframework.healthcheck.HealthChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
final class JavaVirtualMachineHealthCheckerTest {

    @Test
    void getName() {

        // Arrange
        final HealthChecker healthChecker = new JavaVirtualMachineHealthChecker();

        // Act
        final String name = healthChecker.getName();

        // Assert
        Assertions.assertEquals("JVM", name);
    }

    @Test
    void checkHealth() {

        // Arrange
        final HealthChecker healthChecker = new JavaVirtualMachineHealthChecker();

        // Act
        final Health health = healthChecker.checkHealth();

        // Assert
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
