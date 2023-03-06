package dev.voidframework.persistence.jooq;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.datasource.hikaricp.module.HikariCpDataSourceModule;
import dev.voidframework.persistence.jooq.model.Tables;
import dev.voidframework.persistence.jooq.model.UnitTestModel;
import dev.voidframework.persistence.jooq.module.JooqModule;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
final class PersistenceJooqTest {

    private final Injector injector;

    public PersistenceJooqTest() {

        final Config configuration = ConfigFactory.parseString("""
            voidframework.core.runInDevMode = true
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
                install(new JooqModule(configuration));
                bind(Config.class).toInstance(configuration);
            }
        });
    }

    @Test
    void dslContext() {

        // Arrange
        final Provider<DSLContext> dslContextProvider = this.injector.getProvider(DSLContext.class);
        final DSLContext dslContext = dslContextProvider.get();

        // Act
        final Result<Record> result = dslContext.resultQuery("CALL NOW()").fetch();

        // Assert
        Assertions.assertEquals(1, result.size());

        final Timestamp resultValue = (Timestamp) result.get(0).getValue(0);
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), resultValue.toLocalDateTime().toLocalDate());
    }

    @Test
    void transaction() {

        // Arrange
        final Provider<DSLContext> dslContextProvider = this.injector.getProvider(DSLContext.class);
        final DSLContext dslContext = dslContextProvider.get();

        // Act
        dslContext.transaction((context) -> {
            context.dsl().query("""
                CREATE TABLE UNIT_TEST_OLD (
                    ID  VARCHAR(36)   NOT NULL,
                    PRIMARY KEY (id)
                );
                """)
                .execute();

            context.dsl().query("INSERT INTO UNIT_TEST_OLD (ID) VALUES ('93911798-2b0a-46c1-9c91-731c4e6b056a');").execute();
        });

        final Result<Record> result = dslContext.resultQuery("SELECT ID FROM UNIT_TEST_OLD WHERE ROWNUM <= 1").fetch();

        // Assert
        Assertions.assertEquals(1, result.size());

        final String resultValue = (String) result.get(0).getValue(0);
        Assertions.assertEquals("93911798-2b0a-46c1-9c91-731c4e6b056a", resultValue);
    }

    @Test
    void managedEntityQuery() {

        // Arrange
        final Provider<DSLContext> dslContextProvider = this.injector.getProvider(DSLContext.class);
        final DSLContext dslContext = dslContextProvider.get();

        // Act
        final int insertedCount = dslContext.transactionResult((context) -> {
            try {
                context.dsl().query("""
                        CREATE TABLE UNIT_TEST_MANAGED_ENTITY (
                            ID  VARCHAR(36)   NOT NULL,
                            PRIMARY KEY (id)
                        );
                        """)
                    .execute();
            } catch (final DataAccessException ignore) {
                context.dsl().query("TRUNCATE TABLE UNIT_TEST_MANAGED_ENTITY;").execute();
            }

            return context.dsl().insertInto(Tables.UNIT_TEST_MANAGED_ENTITY)
                .set(Tables.UNIT_TEST_MANAGED_ENTITY.ID, "e1f1ea08-4f59-4f5e-83d1-b1ed141d06e7")
                .execute();
        });

        final List<UnitTestModel> resultList = dslContext
            .select()
            .from(Tables.UNIT_TEST_MANAGED_ENTITY)
            .fetchInto(UnitTestModel.class);

        // Assert
        Assertions.assertEquals(1, insertedCount);
        Assertions.assertEquals(1, resultList.size());
        Assertions.assertEquals("e1f1ea08-4f59-4f5e-83d1-b1ed141d06e7", resultList.get(0).id);
    }
}
