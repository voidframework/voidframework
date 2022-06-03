package dev.voidframework.persistence.jpa;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.datasource.hikaricp.module.HikariCpDataSourceModule;
import dev.voidframework.persistence.jpa.model.UnitTest;
import dev.voidframework.persistence.jpa.module.JpaModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class PersistenceJpaTest {

    private final Injector injector;

    public PersistenceJpaTest() {
        final Config configuration = ConfigFactory.parseString("""
            voidframework.core.runInDevMode = true
            voidframework.datasource.default.driver = "org.h2.Driver"
            voidframework.datasource.default.url = "jdbc:h2:mem:unit_tests;MODE=PostgreSQL;DATABASE_TO_UPPER=TRUE;"
            voidframework.datasource.default.username = "sa"
            voidframework.datasource.default.password = "sa"
            voidframework.datasource.default.cachePrepStmts = true
            voidframework.datasource.default.prepStmtCacheSize = 250
            voidframework.datasource.default.prepStmtCacheSqlLimit = 2048
            voidframework.datasource.default.autoCommit = false
            voidframework.datasource.default.connectionInitSql = "SELECT 1 FROM DUAL"
            voidframework.datasource.default.connectionTestQuery = "SELECT 1 FROM DUAL"
            voidframework.datasource.default.connectionTimeout = 10000
            voidframework.datasource.default.idleTimeout = 30000
            voidframework.datasource.default.keepaliveTime = 0
            voidframework.datasource.default.minimumIdle = 1
            voidframework.datasource.default.maximumPoolSize = 15
            """);
        this.injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                install(new HikariCpDataSourceModule());
                install(new JpaModule(configuration));
                bind(Config.class).toInstance(configuration);
            }
        });
    }

    @Test
    public void entityFactory() {
        final Provider<EntityManager> entityManagerProvider = this.injector.getProvider(EntityManager.class);
        Assertions.assertNotNull(entityManagerProvider);

        final EntityManager entityManager = entityManagerProvider.get();
        Assertions.assertNotNull(entityManagerProvider);

        final Integer result = (Integer) entityManager.createNativeQuery("SELECT 1 FROM DUAL").getSingleResult();
        Assertions.assertEquals(1, result);

        entityManager.close();
        Assertions.assertFalse(entityManager.isOpen());
    }

    @Test
    public void transaction() {
        final Provider<EntityManager> entityManagerProvider = this.injector.getProvider(EntityManager.class);
        Assertions.assertNotNull(entityManagerProvider);

        final EntityManager entityManager = entityManagerProvider.get();
        Assertions.assertNotNull(entityManagerProvider);

        final EntityTransaction entityTransaction = entityManager.getTransaction();
        Assertions.assertFalse(entityTransaction.isActive());

        entityTransaction.begin();
        Assertions.assertTrue(entityTransaction.isActive());

        entityManager.createNativeQuery("""
                CREATE TABLE UNIT_TEST_OLD (
                    ID  VARCHAR(36)   NOT NULL,
                    PRIMARY KEY (id)
                );

                INSERT INTO UNIT_TEST_OLD (ID) VALUES ('f0288318-9ef8-4093-85c8-ba6cf5bf6fe5');
                """)
            .executeUpdate();

        entityTransaction.commit();
        Assertions.assertFalse(entityTransaction.isActive());

        final Object result = entityManager.createNativeQuery("SELECT ID FROM UNIT_TEST_OLD WHERE ROWNUM <= 1").getSingleResult();
        Assertions.assertEquals("f0288318-9ef8-4093-85c8-ba6cf5bf6fe5", result);

        entityManager.close();
        Assertions.assertFalse(entityManager.isOpen());
    }

    @Test
    public void managedEntityQuery() {
        final Provider<EntityManager> entityManagerProvider = this.injector.getProvider(EntityManager.class);
        Assertions.assertNotNull(entityManagerProvider);

        final EntityManager entityManager = entityManagerProvider.get();
        Assertions.assertNotNull(entityManagerProvider);

        final EntityTransaction entityTransaction = entityManager.getTransaction();
        Assertions.assertFalse(entityTransaction.isActive());

        entityTransaction.begin();
        Assertions.assertTrue(entityTransaction.isActive());

        entityManager.createNativeQuery("""
                CREATE TABLE UNIT_TEST (
                    ID  VARCHAR(36)   NOT NULL,
                    PRIMARY KEY (id)
                );

                INSERT INTO UNIT_TEST (ID) VALUES ('494f6610-116f-48c9-bd23-764fcd0e0bfc');
                """)
            .executeUpdate();

        entityTransaction.commit();
        Assertions.assertFalse(entityTransaction.isActive());

        final UnitTest result = entityManager.createQuery("SELECT x FROM UnitTest x", UnitTest.class).getSingleResult();
        Assertions.assertNotNull(result);
        Assertions.assertEquals("494f6610-116f-48c9-bd23-764fcd0e0bfc", result.id);

        entityManager.close();
        Assertions.assertFalse(entityManager.isOpen());
    }
}
