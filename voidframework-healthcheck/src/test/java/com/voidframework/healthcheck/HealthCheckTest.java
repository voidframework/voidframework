package com.voidframework.healthcheck;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.voidframework.core.helper.Reflection;
import com.voidframework.healthcheck.checker.JavaVirtualMachineHealthChecker;
import com.voidframework.healthcheck.module.HealthCheckModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@TestMethodOrder(MethodOrderer.MethodName.class)
public final class HealthCheckTest {

    @Test
    public void test() {
        final Config configuration = ConfigFactory.parseString("voidframework.core.runInDevMode=true");
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                install(new HealthCheckModule(configuration));
                bind(DummyChecker.class).asEagerSingleton();
            }
        });

        final HealthCheckManager healthCheckManager = injector.getInstance(HealthCheckManager.class);
        Assertions.assertNotNull(healthCheckManager);

        final List<HealthChecker> healthCheckerList = Reflection.getFieldValue(healthCheckManager, "healthCheckerList", new Reflection.WrappedClass<>());
        Assertions.assertNotNull(healthCheckerList);
        Assertions.assertEquals(2, healthCheckerList.size());
        Assertions.assertTrue(healthCheckerList.get(0) instanceof JavaVirtualMachineHealthChecker);
        Assertions.assertTrue(healthCheckerList.get(1) instanceof DummyChecker);

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
