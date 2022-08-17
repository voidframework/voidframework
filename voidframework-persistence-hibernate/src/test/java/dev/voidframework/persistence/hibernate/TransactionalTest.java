package dev.voidframework.persistence.hibernate;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.datasource.hikaricp.module.HikariCpDataSourceModule;
import dev.voidframework.persistence.hibernate.module.HibernateModule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TransactionRequiredException;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class TransactionalTest {

    private final Injector injector;

    public TransactionalTest() {

        final Config configuration = ConfigFactory.parseString("""
            voidframework.core.runInDevMode = true
            voidframework.persistence.modelsJarUrlPattern = "^.*void.*$"
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
            voidframework.datasource.default.maximumPoolSize = 5
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
    void transactionalMandatoryWithTransaction() {

        final DummyService dummyService = this.injector.getInstance(DummyService.class);
        dummyService.callInnerTransaction(dummyService::transactionalMandatoryWithTransaction);
    }

    @Test
    void transactionalMandatoryWithoutTransaction() {

        final DummyService dummyService = this.injector.getInstance(DummyService.class);
        final TransactionRequiredException exception = Assertions.assertThrows(
            TransactionRequiredException.class,
            dummyService::transactionalMandatoryWithoutTransaction);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals(
            "dev.voidframework.persistence.hibernate.TransactionalTest$DummyService::transactionalMandatoryWithoutTransaction called outside a transaction context",
            exception.getMessage());
    }

    @Test
    void transactionalNeverWithTransaction() {

        final DummyService dummyService = this.injector.getInstance(DummyService.class);
        final InvalidTransactionException exception = Assertions.assertThrows(
            InvalidTransactionException.class,
            () -> dummyService.callInnerTransaction(dummyService::transactionalNeverWithTransaction));

        Assertions.assertNotNull(exception);
        Assertions.assertEquals(
            "dev.voidframework.persistence.hibernate.TransactionalTest$DummyService::transactionalNeverWithTransaction called inside a transaction context",
            exception.getMessage());
    }

    @Test
    void transactionalNeverWithoutTransaction() {

        final DummyService dummyService = this.injector.getInstance(DummyService.class);
        dummyService.transactionalNeverWithoutTransaction();
    }

    @Test
    void transactionalNotSupportedWithTransaction() {

        final DummyService dummyService = this.injector.getInstance(DummyService.class);
        dummyService.callInnerTransaction(dummyService::transactionalNotSupportedWithTransaction);
    }

    @Test
    void transactionalNotSupportedWithoutTransaction() {

        final DummyService dummyService = this.injector.getInstance(DummyService.class);
        dummyService.transactionalNotSupportedWithoutTransaction();
    }

    @Test
    void transactionalRequiredNewWithTransaction() {

        final DummyService dummyService = this.injector.getInstance(DummyService.class);
        dummyService.callInnerTransaction(dummyService::transactionalRequiredNewWithTransaction);
    }

    @Test
    void transactionalRequiredNewWithoutTransaction() {

        final DummyService dummyService = this.injector.getInstance(DummyService.class);
        dummyService.transactionalRequiredNewWithoutTransaction();
    }

    @Test
    void transactionalRequiredWithTransaction() {

        final DummyService dummyService = this.injector.getInstance(DummyService.class);
        dummyService.callInnerTransaction(dummyService::transactionalRequiredWithTransaction);
    }

    @Test
    void transactionalRequiredWithoutTransaction() {

        final DummyService dummyService = this.injector.getInstance(DummyService.class);
        dummyService.transactionalRequiredWithoutTransaction();
    }

    @Test
    void transactionalSupportWithTransaction() {

        final DummyService dummyService = this.injector.getInstance(DummyService.class);
        dummyService.callInnerTransaction(dummyService::transactionalSupportWithTransaction);
    }

    @Test
    void transactionalSupportWithoutTransaction() {

        final DummyService dummyService = this.injector.getInstance(DummyService.class);
        dummyService.transactionalSupportWithoutTransaction();
    }

    @Test
    void transactionalRollbackOnUncheckedException() {

        // Creates table
        final Provider<EntityManager> entityManagerProvider = this.injector.getProvider(EntityManager.class);
        final EntityManager entityManager = entityManagerProvider.get();
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager
            .createNativeQuery("""
                CREATE TABLE UNIT_TEST_UNCHECKED_EX (
                    ID  VARCHAR(36)   NOT NULL,
                    PRIMARY KEY (id)
                );
                """)
            .executeUpdate();
        transaction.commit();

        try {
            final DummyService dummyService = this.injector.getInstance(DummyService.class);
            dummyService.transactionalRequiredUncheckedException();
        } catch (final NullPointerException ignore) {
        }

        final List<?> list = entityManagerProvider.get().createNativeQuery("SELECT * FROM UNIT_TEST_UNCHECKED_EX").getResultList();
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void transactionalDontRollbackOnCheckedException() {

        // Creates table
        final Provider<EntityManager> entityManagerProvider = this.injector.getProvider(EntityManager.class);
        final EntityManager entityManager = entityManagerProvider.get();
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager
            .createNativeQuery("""
                CREATE TABLE UNIT_TEST_CHECKED_EX (
                    ID  VARCHAR(36)   NOT NULL,
                    PRIMARY KEY (id)
                );
                """)
            .executeUpdate();
        transaction.commit();

        try {
            final DummyService dummyService = this.injector.getInstance(DummyService.class);
            dummyService.transactionalRequiredCheckedException();
        } catch (final FileNotFoundException ignore) {
        }

        final List<?> list = entityManagerProvider.get().createNativeQuery("SELECT * FROM UNIT_TEST_CHECKED_EX").getResultList();
        Assertions.assertFalse(list.isEmpty());
    }

    public static class DummyService {

        private final Provider<EntityManager> entityManagerProvider;

        @Inject
        public DummyService(final Provider<EntityManager> entityManagerProvider) {

            this.entityManagerProvider = entityManagerProvider;
        }

        @Transactional(Transactional.TxType.REQUIRED)
        void callInnerTransaction(final Consumer<EntityTransaction> consumer) {

            final EntityTransaction transaction = this.entityManagerProvider.get().getTransaction();
            consumer.accept(transaction);
        }

        @Transactional(Transactional.TxType.MANDATORY)
        void transactionalMandatoryWithTransaction(final EntityTransaction parentTransaction) {

            final EntityManager entityManager = this.entityManagerProvider.get();
            Assertions.assertTrue(entityManager.isJoinedToTransaction());
            Assertions.assertEquals(parentTransaction, entityManager.getTransaction());
        }

        @Transactional(Transactional.TxType.MANDATORY)
        void transactionalMandatoryWithoutTransaction() {
            /* The code will never be run */
        }

        @Transactional(Transactional.TxType.NEVER)
        void transactionalNeverWithTransaction(final EntityTransaction parentTransaction) {
            /* The code will never be run */
        }

        @Transactional(Transactional.TxType.NEVER)
        void transactionalNeverWithoutTransaction() {

            final EntityManager entityManager = this.entityManagerProvider.get();
            Assertions.assertFalse(entityManager.isJoinedToTransaction());
        }

        @Transactional(Transactional.TxType.NOT_SUPPORTED)
        void transactionalNotSupportedWithTransaction(final EntityTransaction parentTransaction) {

            final EntityManager entityManager = this.entityManagerProvider.get();
            Assertions.assertFalse(entityManager.isJoinedToTransaction());
            Assertions.assertNotEquals(parentTransaction, entityManager.getTransaction());
        }

        @Transactional(Transactional.TxType.NOT_SUPPORTED)
        void transactionalNotSupportedWithoutTransaction() {

            final EntityManager entityManager = this.entityManagerProvider.get();
            Assertions.assertFalse(entityManager.isJoinedToTransaction());
        }

        @Transactional(Transactional.TxType.SUPPORTS)
        void transactionalSupportWithTransaction(final EntityTransaction parentTransaction) {

            final EntityManager entityManager = this.entityManagerProvider.get();
            Assertions.assertTrue(entityManager.isJoinedToTransaction());
            Assertions.assertEquals(parentTransaction, entityManager.getTransaction());
        }

        @Transactional(Transactional.TxType.SUPPORTS)
        void transactionalSupportWithoutTransaction() {

            final EntityManager entityManager = this.entityManagerProvider.get();
            Assertions.assertFalse(entityManager.isJoinedToTransaction());
        }

        @Transactional(Transactional.TxType.REQUIRES_NEW)
        void transactionalRequiredNewWithTransaction(final EntityTransaction parentTransaction) {

            final EntityManager entityManager = this.entityManagerProvider.get();
            Assertions.assertTrue(entityManager.isJoinedToTransaction());
            Assertions.assertNotEquals(parentTransaction, entityManager.getTransaction());
        }

        @Transactional(Transactional.TxType.REQUIRES_NEW)
        void transactionalRequiredNewWithoutTransaction() {

            final EntityManager entityManager = this.entityManagerProvider.get();
            Assertions.assertTrue(entityManager.isJoinedToTransaction());
        }

        @Transactional(Transactional.TxType.REQUIRED)
        void transactionalRequiredWithTransaction(final EntityTransaction parentTransaction) {

            final EntityManager entityManager = this.entityManagerProvider.get();
            Assertions.assertTrue(entityManager.isJoinedToTransaction());
            Assertions.assertEquals(parentTransaction, entityManager.getTransaction());

            final String tableSuffix = UUID.randomUUID().toString().substring(0, 8);

            entityManager
                .createNativeQuery("""
                    CREATE TABLE UNIT_TEST_%s (
                        ID  VARCHAR(36)   NOT NULL,
                        PRIMARY KEY (id)
                    );

                    INSERT INTO UNIT_TEST_%s (ID) VALUES ('f0288318-9ef8-4093-85c8-ba6cf5bf6fe5');
                    """.formatted(tableSuffix, tableSuffix))
                .executeUpdate();
        }

        @Transactional(Transactional.TxType.REQUIRED)
        void transactionalRequiredWithoutTransaction() {

            final EntityManager entityManager = this.entityManagerProvider.get();
            Assertions.assertTrue(entityManager.isJoinedToTransaction());

            final String tableSuffix = UUID.randomUUID().toString().substring(0, 8);

            entityManager
                .createNativeQuery("""
                    CREATE TABLE UNIT_TEST_%s (
                        ID  VARCHAR(36)   NOT NULL,
                        PRIMARY KEY (id)
                    );

                    INSERT INTO UNIT_TEST_%s (ID) VALUES ('f0288318-9ef8-4093-85c8-ba6cf5bf6fe5');
                    """.formatted(tableSuffix, tableSuffix))
                .executeUpdate();
        }

        @Transactional(Transactional.TxType.REQUIRED)
        void transactionalRequiredUncheckedException() {

            final EntityManager entityManager = this.entityManagerProvider.get();
            Assertions.assertTrue(entityManager.isJoinedToTransaction());

            entityManager
                .createNativeQuery("""
                    INSERT INTO UNIT_TEST_CHECKED_EX (ID) VALUES ('f0288318-9ef8-4093-85c8-ba6cf5bf6fe5');
                    """)
                .executeUpdate();

            throw new NullPointerException();
        }

        @Transactional(Transactional.TxType.REQUIRED)
        void transactionalRequiredCheckedException() throws FileNotFoundException {

            final EntityManager entityManager = this.entityManagerProvider.get();
            Assertions.assertTrue(entityManager.isJoinedToTransaction());

            entityManager
                .createNativeQuery("""
                    INSERT INTO UNIT_TEST_CHECKED_EX (ID) VALUES ('17930add-5049-4ae4-9a7e-bf826ed160bc');
                    """)
                .executeUpdate();

            throw new FileNotFoundException();
        }
    }
}
