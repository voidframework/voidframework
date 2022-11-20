package dev.voidframework.persistence.jooq;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.datasource.hikaricp.module.HikariCpDataSourceModule;
import dev.voidframework.persistence.jooq.model.Tables;
import dev.voidframework.persistence.jooq.model.UnitTestModel;
import dev.voidframework.persistence.jooq.module.JooqModule;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
final class TransactionalTest {

    private final Injector injector;

    public TransactionalTest() {

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
                install(new JooqModule(configuration));
                bind(Config.class).toInstance(configuration);
                bind(ServiceTransactionnal.class).asEagerSingleton();
            }
        });

        final Provider<DSLContext> dslContextProvider = this.injector.getProvider(DSLContext.class);
        try {
            dslContextProvider.get().transaction((context) -> context.dsl().query("""
                    CREATE TABLE UNIT_TEST_MANAGED_ENTITY (
                        ID  VARCHAR(36)   NOT NULL,
                        PRIMARY KEY (id)
                    );
                    """)
                .execute());
        } catch (final DataAccessException ignore) {
            // Tables already created
        }
    }

    @Test
    void txRequired() {

        // Arrange
        final ServiceTransactionnal serviceTransactionnal = this.injector.getInstance(ServiceTransactionnal.class);
        final Provider<DSLContext> dslContextProvider = this.injector.getProvider(DSLContext.class);

        // Act
        serviceTransactionnal.txRequired();

        final List<UnitTestModel> unitTestModelList = dslContextProvider.get()
            .select()
            .from(Tables.UNIT_TEST_MANAGED_ENTITY)
            .where(Tables.UNIT_TEST_MANAGED_ENTITY.ID.eq("093d5938-d41b-419b-829e-4acdf2f01a74"))
            .fetchInto(UnitTestModel.class);

        // Assert
        Assertions.assertNotNull(unitTestModelList);
        Assertions.assertFalse(unitTestModelList.isEmpty());
        Assertions.assertEquals("093d5938-d41b-419b-829e-4acdf2f01a74", unitTestModelList.get(0).id);
    }

    @Test
    void txRequiredWithException() {

        // Arrange
        final ServiceTransactionnal serviceTransactionnal = this.injector.getInstance(ServiceTransactionnal.class);
        final Provider<DSLContext> dslContextProvider = this.injector.getProvider(DSLContext.class);

        // Act
        Assertions.assertThrows(RuntimeException.class, serviceTransactionnal::txRequiredWithException);

        final List<UnitTestModel> unitTestModelList = dslContextProvider.get()
            .select()
            .from(Tables.UNIT_TEST_MANAGED_ENTITY)
            .where(Tables.UNIT_TEST_MANAGED_ENTITY.ID.eq("0872e9d5-1cc1-46cd-8873-ce91ef3af539"))
            .fetchInto(UnitTestModel.class);

        // Assert
        Assertions.assertNotNull(unitTestModelList);
        Assertions.assertTrue(unitTestModelList.isEmpty());
    }

    @Test
    void txRequiresNew() {

        // Arrange
        final ServiceTransactionnal serviceTransactionnal = this.injector.getInstance(ServiceTransactionnal.class);
        final Provider<DSLContext> dslContextProvider = this.injector.getProvider(DSLContext.class);

        // Act
        Assertions.assertThrows(RuntimeException.class, serviceTransactionnal::txRequiresNew);

        final List<UnitTestModel> unitTestModelList = dslContextProvider.get()
            .select()
            .from(Tables.UNIT_TEST_MANAGED_ENTITY)
            .where(Tables.UNIT_TEST_MANAGED_ENTITY.ID.eq("6e60b038-f1a8-48fd-a002-c7e96ed9dc00"))
            .fetchInto(UnitTestModel.class);

        // Assert
        Assertions.assertNotNull(unitTestModelList);
        Assertions.assertFalse(unitTestModelList.isEmpty());
        Assertions.assertEquals("6e60b038-f1a8-48fd-a002-c7e96ed9dc00", unitTestModelList.get(0).id);
    }

    @Test
    void txNever() {

        // Arrange
        final ServiceTransactionnal serviceTransactionnal = this.injector.getInstance(ServiceTransactionnal.class);

        // Act
        serviceTransactionnal.txNever();

        // Assert
        Assertions.assertTrue(true);
    }

    @Test
    void txNeverViolated() {

        // Arrange
        final ServiceTransactionnal serviceTransactionnal = this.injector.getInstance(ServiceTransactionnal.class);

        // Act
        final InvalidTransactionException exception = Assertions.assertThrows(
            InvalidTransactionException.class,
            serviceTransactionnal::txNeverViolated);

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(
            "dev.voidframework.persistence.jooq.TransactionalTest$ServiceTransactionnal::doSomethingWithTxNever called inside a transaction context",
            exception.getMessage());
    }

    /**
     * Dummy service.
     */
    public static class ServiceTransactionnal {

        @Inject
        private Provider<DSLContext> dslContextProvider;

        @Transactional(Transactional.TxType.NEVER)
        public void doSomethingWithTxNever() {
        }

        @Transactional(Transactional.TxType.REQUIRES_NEW)
        public void doSomethingWithTxRequiresNew() {

            final DSLContext dslContext = this.dslContextProvider.get();
            dslContext.insertInto(Tables.UNIT_TEST_MANAGED_ENTITY)
                .set(Tables.UNIT_TEST_MANAGED_ENTITY.ID, "6e60b038-f1a8-48fd-a002-c7e96ed9dc00")
                .execute();
        }

        @Transactional(Transactional.TxType.REQUIRED)
        public void txRequired() {

            final DSLContext dslContext = this.dslContextProvider.get();
            dslContext.insertInto(Tables.UNIT_TEST_MANAGED_ENTITY)
                .set(Tables.UNIT_TEST_MANAGED_ENTITY.ID, "093d5938-d41b-419b-829e-4acdf2f01a74")
                .execute();
        }

        @Transactional(Transactional.TxType.REQUIRED)
        public void txRequiredWithException() {

            final DSLContext dslContext = this.dslContextProvider.get();
            dslContext.insertInto(Tables.UNIT_TEST_MANAGED_ENTITY)
                .set(Tables.UNIT_TEST_MANAGED_ENTITY.ID, "0872e9d5-1cc1-46cd-8873-ce91ef3af539")
                .execute();
            throw new RuntimeException("Oops");
        }

        @Transactional(Transactional.TxType.REQUIRED)
        public void txRequiresNew() {

            doSomethingWithTxRequiresNew();
            throw new RuntimeException("Oops");
        }

        @Transactional(Transactional.TxType.NEVER)
        public void txNever() {

            doSomethingWithTxNever();
        }

        @Transactional(Transactional.TxType.REQUIRED)
        public void txNeverViolated() {

            doSomethingWithTxNever();
        }
    }
}
