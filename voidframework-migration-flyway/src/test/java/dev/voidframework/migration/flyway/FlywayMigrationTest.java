package dev.voidframework.migration.flyway;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.datasource.DataSourceManager;
import dev.voidframework.datasource.hikaricp.module.HikariCpDataSourceModule;
import dev.voidframework.migration.flyway.module.FlywayMigrationModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class FlywayMigrationTest {

    private final Injector injector;

    public FlywayMigrationTest() {

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
            voidframework.migration.flyway.callbacks = []
            voidframework.migration.flyway.historySchemaTable = "FLYWAY_MIGRATION"
            voidframework.migration.flyway.historySchemaTablespace = ""
            voidframework.migration.flyway.outOfOrder = false
            voidframework.migration.flyway.placeholderReplacement = true
            voidframework.migration.flyway.scriptLocations = ["migrations"]
            """);
        this.injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {

            @Override
            protected void configure() {

                install(new HikariCpDataSourceModule());
                install(new FlywayMigrationModule());
                bind(Config.class).toInstance(configuration);
            }
        });
    }

    @Test
    public void migration() throws SQLException {

        final FlywayMigration flywayMigration = this.injector.getInstance(FlywayMigration.class);
        Assertions.assertNotNull(flywayMigration);

        Assertions.assertDoesNotThrow(flywayMigration::migrate);

        final DataSourceManager dataSourceManager = this.injector.getInstance(DataSourceManager.class);
        Assertions.assertNotNull(dataSourceManager);

        final Connection connection = Assertions.assertDoesNotThrow(() -> dataSourceManager.getConnection("default"));
        final ResultSet resultSet = connection.prepareStatement("SELECT * FROM FLYWAY_MIGRATION").executeQuery();

        final Map<String, Object> map = new HashMap<>();
        final ResultSetMetaData meta = resultSet.getMetaData();
        final int colCount = meta.getColumnCount();
        while (resultSet.next()) {
            for (int col = 1; col <= colCount; col++) {
                final String columnName = meta.getColumnName(col);
                final Object value = resultSet.getObject(col);
                map.put(columnName, value);
            }
        }

        Assertions.assertEquals("SA", map.get("installed_by"));
        Assertions.assertEquals(true, map.get("success"));
        Assertions.assertEquals(958041986, map.get("checksum"));
        Assertions.assertEquals("initial-structure", map.get("description"));
        Assertions.assertEquals("SQL", map.get("type"));
        Assertions.assertEquals("1.0.0.0", map.get("version"));
        Assertions.assertEquals("V1.0.0.0__initial-structure.sql", map.get("script"));
        Assertions.assertEquals(1, map.get("installed_rank"));
        Assertions.assertEquals(
            LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
            ((Timestamp) map.get("installed_on")).toLocalDateTime().truncatedTo(ChronoUnit.MINUTES));

        resultSet.close();
        connection.close();
    }
}
