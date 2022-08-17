package dev.voidframework.healthcheck;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import dev.voidframework.core.helper.Reflection;
import dev.voidframework.healthcheck.checker.JavaVirtualMachineHealthChecker;
import dev.voidframework.healthcheck.module.HealthCheckModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
final class HealthCheckTest {

    @Test
    void test() {

        // Arrange + Act
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {

                install(new HealthCheckModule());
                bind(JavaVirtualMachineHealthChecker.class).asEagerSingleton();
                bind(DummyChecker.class).asEagerSingleton();
            }
        });

        final HealthCheckManager healthCheckManager = injector.getInstance(HealthCheckManager.class);

        // Assert
        Assertions.assertNotNull(healthCheckManager);

        final List<Class<? extends HealthChecker>> healthCheckerList = Reflection.getFieldValue(
            healthCheckManager,
            "healthCheckerList",
            new Reflection.WrappedClass<>());
        Assertions.assertNotNull(healthCheckerList);
        Assertions.assertEquals(2, healthCheckerList.size());
        Assertions.assertEquals(healthCheckerList.get(0), JavaVirtualMachineHealthChecker.class);
        Assertions.assertEquals(healthCheckerList.get(1), DummyChecker.class);

        final Map<String, Health> healthPerNameMap = healthCheckManager.checkHealth();
        Assertions.assertNotNull(healthPerNameMap);
        Assertions.assertEquals(2, healthPerNameMap.size());
        Assertions.assertTrue(healthPerNameMap.keySet().containsAll(Arrays.asList("JVM", "DUMMY_CHECKER")));
        Assertions.assertFalse(healthPerNameMap.values().stream().filter(Objects::nonNull).toList().isEmpty());
    }

    public static final class DummyChecker implements HealthChecker {

        @Override
        public String getName() {

            return "DUMMY_CHECKER";
        }

        @Override
        public Health checkHealth() {

            return new Health(Health.Status.UP, Collections.emptyMap());
        }
    }
}
