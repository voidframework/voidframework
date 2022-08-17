package dev.voidframework.datasource.c3p0;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.datasource.DataSourceManager;
import dev.voidframework.datasource.c3p0.module.C3P0DataSourceModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public final class DataSourceTest {

    private final Injector injector;

    public DataSourceTest() {

        final Config configuration = ConfigFactory.parseString("""
            voidframework.core.runInDevMode = true
            voidframework.datasource.default.driver = "org.h2.Driver"
            voidframework.datasource.default.url = "jdbc:h2:mem:unit_tests;MODE=PostgreSQL;DATABASE_TO_UPPER=TRUE;"
            voidframework.datasource.default.username = "sa"
            voidframework.datasource.default.password = "sa"
            voidframework.datasource.second.driver = "org.h2.Driver"
            voidframework.datasource.second.url = "jdbc:h2:mem:unit_tests;MODE=PostgreSQL;DATABASE_TO_UPPER=TRUE;"
            voidframework.datasource.second.username = "sa"
            voidframework.datasource.second.password = "sa"
            """);
        this.injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                install(new C3P0DataSourceModule());
                bind(Config.class).toInstance(configuration);
            }
        });
    }

    @Test
    void defaultConnection() throws SQLException {

        // Arrange
        final DataSourceManager dataSourceManager = this.injector.getInstance(DataSourceManager.class);

        // Act
        final Connection connection = dataSourceManager.getConnection();

        // Assert
        Assertions.assertNotNull(connection);

        // Epilogue
        connection.close();
    }

    @Test
    void specificConnection() throws SQLException {

        // Arrange
        final DataSourceManager dataSourceManager = this.injector.getInstance(DataSourceManager.class);

        // Act
        final Connection connection = dataSourceManager.getConnection("second");

        // Assert
        Assertions.assertNotNull(connection);

        // Epilogue
        connection.close();
    }

    @Test
    void unknownConnection() throws SQLException {

        // Arrange
        final DataSourceManager dataSourceManager = this.injector.getInstance(DataSourceManager.class);

        // Act
        final Connection connection = dataSourceManager.getConnection("unknown");

        // Assert
        Assertions.assertNull(connection);
    }

    @Test
    void defaultDataSource() {

        // Arrange
        final DataSourceManager dataSourceManager = this.injector.getInstance(DataSourceManager.class);

        // Act
        final DataSource dataSource = dataSourceManager.getDataSource();

        // Assert
        Assertions.assertTrue(dataSource instanceof ComboPooledDataSource);
    }


    @Test
    void specificDataSource() {

        // Arrange
        final DataSourceManager dataSourceManager = this.injector.getInstance(DataSourceManager.class);

        // Act
        final DataSource dataSource = dataSourceManager.getDataSource("second");

        // Assert
        Assertions.assertTrue(dataSource instanceof ComboPooledDataSource);
    }

    @Test
    void unknownDataSource() {

        // Arrange
        final DataSourceManager dataSourceManager = this.injector.getInstance(DataSourceManager.class);

        // Act
        final DataSource dataSource = dataSourceManager.getDataSource("unknown");

        // Assert
        Assertions.assertNull(dataSource);
    }
}
