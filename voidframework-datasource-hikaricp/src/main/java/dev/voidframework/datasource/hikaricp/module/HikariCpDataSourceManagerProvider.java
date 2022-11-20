package dev.voidframework.datasource.hikaricp.module;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.voidframework.datasource.AbstractDataSourceProvider;
import dev.voidframework.datasource.DataSourceManager;
import dev.voidframework.datasource.exception.DataSourceException;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * HikariCP data source manager provider.
 */
@Singleton
public class HikariCpDataSourceManagerProvider extends AbstractDataSourceProvider {

    private static final String HIKARICP_PROPERTIES_CACHE_PREPARED_STATEMENTS = "cachePrepStmts";
    private static final String HIKARICP_PROPERTIES_PREPARED_STATEMENT_CACHE_SIZE = "prepStmtCacheSize";
    private static final String HIKARICP_PROPERTIES_PREPARED_STATEMENT_CACHE_SQL_LIMIT = "prepStmtCacheSqlLimit";

    private static final String CONFIGURATION_KEY_DRIVER_CLASS = "driver";
    private static final String CONFIGURATION_KEY_URL = "url";
    private static final String CONFIGURATION_KEY_USERNAME = "username";
    private static final String CONFIGURATION_KEY_PASSWORD = "password";
    private static final String CONFIGURATION_KEY_CACHE_PREPARED_STATEMENTS = "cachePrepStmts";
    private static final String CONFIGURATION_KEY_PREPARED_STATEMENT_CACHE_SIZE = "prepStmtCacheSize";
    private static final String CONFIGURATION_KEY_PREPARED_STATEMENT_CACHE_SQL_LIMIT = "prepStmtCacheSqlLimit";
    private static final String CONFIGURATION_KEY_CONNECTION_TIMEOUT = "connectionTimeout";
    private static final String CONFIGURATION_KEY_IDLE_TIMEOUT = "idleTimeout";
    private static final String CONFIGURATION_KEY_KEEPALIVE_TIME = "keepaliveTime";
    private static final String CONFIGURATION_KEY_CONNECTION_INITIALISATION_SQL_QUERY = "connectionInitSql";
    private static final String CONFIGURATION_KEY_CONNECTION_TEST_SQL_QUERY = "connectionTestQuery";
    private static final String CONFIGURATION_KEY_AUTO_COMMIT = "autoCommit";
    private static final String CONFIGURATION_KEY_CONNECTION_POOL_MINIMUM_IDLE = "minimumIdle";
    private static final String CONFIGURATION_KEY_CONNECTION_POOL_MAXIMUM_SIZE = "maximumPoolSize";
    private static final String CONFIGURATION_KEY_MAXIMUM_CONNECTION_AGE = "maxConnectionAge";

    private final Config configuration;
    private DataSourceManager dataSourceManager;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    @Inject
    private HikariCpDataSourceManagerProvider(final Config configuration) {

        this.configuration = configuration;
    }

    @Override
    public DataSourceManager get() {

        // Returns data source manager if existing
        if (dataSourceManager != null) {
            return this.dataSourceManager;
        }

        // Defines the parameters of the data source which are optional
        final Map<String, BiConsumer<HikariConfig, Config>> optionalHikariConfigToApplyMap = new HashMap<>();
        optionalHikariConfigToApplyMap.put(
            CONFIGURATION_KEY_CACHE_PREPARED_STATEMENTS,
            (hikariCfg, appCfg) -> hikariCfg.addDataSourceProperty(
                HIKARICP_PROPERTIES_CACHE_PREPARED_STATEMENTS,
                appCfg.getString(CONFIGURATION_KEY_CACHE_PREPARED_STATEMENTS)));
        optionalHikariConfigToApplyMap.put(
            CONFIGURATION_KEY_PREPARED_STATEMENT_CACHE_SIZE,
            (hikariCfg, appCfg) -> hikariCfg.addDataSourceProperty(
                HIKARICP_PROPERTIES_PREPARED_STATEMENT_CACHE_SIZE,
                appCfg.getString(CONFIGURATION_KEY_PREPARED_STATEMENT_CACHE_SIZE)));
        optionalHikariConfigToApplyMap.put(
            CONFIGURATION_KEY_PREPARED_STATEMENT_CACHE_SQL_LIMIT,
            (hikariCfg, appCfg) -> hikariCfg.addDataSourceProperty(
                HIKARICP_PROPERTIES_PREPARED_STATEMENT_CACHE_SQL_LIMIT,
                appCfg.getString(CONFIGURATION_KEY_PREPARED_STATEMENT_CACHE_SQL_LIMIT)));
        optionalHikariConfigToApplyMap.put(
            CONFIGURATION_KEY_CONNECTION_TIMEOUT,
            (hikariCfg, appCfg) -> hikariCfg.setConnectionTimeout(appCfg.getInt(CONFIGURATION_KEY_CONNECTION_TIMEOUT)));
        optionalHikariConfigToApplyMap.put(
            CONFIGURATION_KEY_IDLE_TIMEOUT,
            (hikariCfg, appCfg) -> hikariCfg.setIdleTimeout(appCfg.getInt(CONFIGURATION_KEY_IDLE_TIMEOUT)));
        optionalHikariConfigToApplyMap.put(
            CONFIGURATION_KEY_KEEPALIVE_TIME,
            (hikariCfg, appCfg) -> hikariCfg.setKeepaliveTime(appCfg.getInt(CONFIGURATION_KEY_KEEPALIVE_TIME)));
        optionalHikariConfigToApplyMap.put(
            CONFIGURATION_KEY_CONNECTION_INITIALISATION_SQL_QUERY,
            (hikariCfg, appCfg) -> hikariCfg.setConnectionInitSql(appCfg.getString(CONFIGURATION_KEY_CONNECTION_INITIALISATION_SQL_QUERY)));
        optionalHikariConfigToApplyMap.put(
            CONFIGURATION_KEY_CONNECTION_TEST_SQL_QUERY,
            (hikariCfg, appCfg) -> hikariCfg.setConnectionTestQuery(appCfg.getString(CONFIGURATION_KEY_CONNECTION_TEST_SQL_QUERY)));
        optionalHikariConfigToApplyMap.put(
            CONFIGURATION_KEY_AUTO_COMMIT,
            (hikariCfg, appCfg) -> hikariCfg.setAutoCommit(appCfg.getBoolean(CONFIGURATION_KEY_AUTO_COMMIT)));
        optionalHikariConfigToApplyMap.put(
            CONFIGURATION_KEY_CONNECTION_POOL_MINIMUM_IDLE,
            (hikariCfg, appCfg) -> hikariCfg.setMinimumIdle(appCfg.getInt(CONFIGURATION_KEY_CONNECTION_POOL_MINIMUM_IDLE)));
        optionalHikariConfigToApplyMap.put(
            CONFIGURATION_KEY_CONNECTION_POOL_MAXIMUM_SIZE,
            (hikariCfg, appCfg) -> hikariCfg.setMaximumPoolSize(appCfg.getInt(CONFIGURATION_KEY_CONNECTION_POOL_MAXIMUM_SIZE)));
        optionalHikariConfigToApplyMap.put(
            CONFIGURATION_KEY_MAXIMUM_CONNECTION_AGE,
            (hikariCfg, appCfg) -> hikariCfg.setMaxLifetime(appCfg.getInt(CONFIGURATION_KEY_MAXIMUM_CONNECTION_AGE)));

        // Configuration of the different data sources
        final Map<String, DataSource> hikariDataSourcePerNameMap = new HashMap<>();
        for (final String dbConfigurationName : this.retrieveDataSourceConfigurationNames(this.configuration)) {

            final Config dbConfiguration = this.configuration.getConfig(PREFIX_CONFIGURATION_KEY_DATASOURCE + dbConfigurationName);

            final HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setPoolName(dbConfigurationName);
            hikariConfig.setJdbcUrl(dbConfiguration.getString(CONFIGURATION_KEY_URL));
            hikariConfig.setUsername(dbConfiguration.getString(CONFIGURATION_KEY_USERNAME));
            hikariConfig.setPassword(dbConfiguration.getString(CONFIGURATION_KEY_PASSWORD));
            try {
                hikariConfig.setDriverClassName(dbConfiguration.getString(CONFIGURATION_KEY_DRIVER_CLASS));
            } catch (final RuntimeException exception) {
                throw new DataSourceException.DriverLoadFailure(dbConfiguration.getString(CONFIGURATION_KEY_DRIVER_CLASS), exception);
            }

            for (final Map.Entry<String, BiConsumer<HikariConfig, Config>> entrySet : optionalHikariConfigToApplyMap.entrySet()) {
                if (dbConfiguration.hasPath(entrySet.getKey())) {
                    entrySet.getValue().accept(hikariConfig, dbConfiguration);
                }
            }

            hikariDataSourcePerNameMap.put(dbConfigurationName, new HikariDataSource(hikariConfig));
        }

        // Create data source manager
        this.dataSourceManager = new DataSourceManager(hikariDataSourcePerNameMap);
        return this.dataSourceManager;
    }
}
