package dev.voidframework.persistence.hibernate;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.datasource.hikaricp.module.HikariCpDataSourceModule;
import dev.voidframework.persistence.hibernate.model.UnitTestModel;
import dev.voidframework.persistence.hibernate.module.HibernateModule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
final class PersistenceHibernateTest {

    private final Injector injector;

    public PersistenceHibernateTest() {

        final Config configuration = ConfigFactory.parseString("""
            voidframework.core.runInDevMode = true
            voidframework.persistence.modelsJarUrlPattern = "^.*void.*$"
            voidframework.datasource.default.driver = "org.hsqldb.jdbc.JDBCDriver"
            voidframework.datasource.default.url = "jdbc:hsqldb:mem:unit_tests;sql.syntax_ora=true"
            voidframework.datasource.default.username = "sa"
            voidframework.datasource.default.password = "sa"
            voidframework.datasource.default.cachePrepStmts = true
            voidframework.datasource.default.prepStmtCacheSize = 250
            voidframework.datasource.default.prepStmtCacheSqlLimit = 2048
            voidframework.datasource.default.autoCommit = false
            voidframework.datasource.default.connectionInitSql = "CALL NOW()"
            voidframework.datasource.default.connectionTestQuery = "CALL NOW()"
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
                install(new HibernateModule(configuration));
                bind(Config.class).toInstance(configuration);
            }
        });
    }

    @Test
    void entityFactory() {

        final Provider<EntityManager> entityManagerProvider = this.injector.getProvider(EntityManager.class);
        Assertions.assertNotNull(entityManagerProvider);

        final EntityManager entityManager = entityManagerProvider.get();
        Assertions.assertNotNull(entityManagerProvider);

        final Timestamp result = (Timestamp) entityManager.createNativeQuery("CALL NOW()").getSingleResult();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), result.toLocalDateTime().toLocalDate());

        entityManager.close();
        Assertions.assertFalse(entityManager.isOpen());
    }

    @Test
    void transaction() {

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
            """).executeUpdate();

        entityManager.createNativeQuery("INSERT INTO UNIT_TEST_OLD (ID) VALUES ('f0288318-9ef8-4093-85c8-ba6cf5bf6fe5');").executeUpdate();

        entityTransaction.commit();
        Assertions.assertFalse(entityTransaction.isActive());

        final Object result = entityManager.createNativeQuery("SELECT ID FROM UNIT_TEST_OLD WHERE ROWNUM <= 1").getSingleResult();
        Assertions.assertEquals("f0288318-9ef8-4093-85c8-ba6cf5bf6fe5", result);

        entityManager.close();
        Assertions.assertFalse(entityManager.isOpen());
    }

    @Test
    void managedEntityQuery() {

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
            """).executeUpdate();
        entityManager.createNativeQuery("INSERT INTO UNIT_TEST (ID) VALUES ('494f6610-116f-48c9-bd23-764fcd0e0bfc');").executeUpdate();

        entityTransaction.commit();
        Assertions.assertFalse(entityTransaction.isActive());

        final UnitTestModel result = entityManager.createQuery("SELECT x FROM UnitTestModel x", UnitTestModel.class).getSingleResult();
        Assertions.assertNotNull(result);
        Assertions.assertEquals("494f6610-116f-48c9-bd23-764fcd0e0bfc", result.id);

        entityManager.close();
        Assertions.assertFalse(entityManager.isOpen());
    }
}
