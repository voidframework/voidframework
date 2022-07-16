package dev.voidframework.datasource.c3p0.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.typesafe.config.Config;
import dev.voidframework.datasource.DataSourceManager;
import dev.voidframework.exception.DataSourceException;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * C3P0 data source manager provider.
 */
@Singleton
public class C3P0DataSourceManagerProvider implements Provider<DataSourceManager> {

    private final Config configuration;
    private DataSourceManager dataSourceManager;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    @Inject
    private C3P0DataSourceManagerProvider(final Config configuration) {

        this.configuration = configuration;
    }

    @Override
    public DataSourceManager get() {

        // Returns data source manager if existin&g
        if (dataSourceManager != null) {
            return this.dataSourceManager;
        }

        // Defines the parameters of the data source which are optional
        final Map<String, BiConsumer<ComboPooledDataSource, Config>> optionalHikariConfigToApplyMap = new HashMap<>();
        optionalHikariConfigToApplyMap.put("connectionTimeout", (c3p0Cfg, appCfg) -> {
            try {
                c3p0Cfg.setLoginTimeout(appCfg.getInt("connectionTimeout") / 1000);
            } catch (final SQLException ignore) {
            }
        });
        optionalHikariConfigToApplyMap.put("prepStmtCacheSize", (c3p0Cfg, appCfg) ->
            c3p0Cfg.setMaxStatements(appCfg.getInt("prepStmtCacheSize")));
        optionalHikariConfigToApplyMap.put("statementCacheNumDeferredCloseThreads", (c3p0Cfg, appCfg) ->
            c3p0Cfg.setStatementCacheNumDeferredCloseThreads(appCfg.getInt("statementCacheNumDeferredCloseThreads")));
        optionalHikariConfigToApplyMap.put("idleTimeout",
            (c3p0Cfg, appCfg) -> c3p0Cfg.setMaxIdleTime(appCfg.getInt("idleTimeout") / 1000));
        optionalHikariConfigToApplyMap.put("maxConnectionAge", (c3p0Cfg, appCfg) ->
            c3p0Cfg.setMaxConnectionAge(appCfg.getInt("maxConnectionAge") / 1000));
        optionalHikariConfigToApplyMap.put("autoCommit", (c3p0Cfg, appCfg) ->
            c3p0Cfg.setAutoCommitOnClose(appCfg.getBoolean("autoCommit")));
        optionalHikariConfigToApplyMap.put("minimumIdle", (c3p0Cfg, appCfg) -> {
            c3p0Cfg.setMinPoolSize(appCfg.getInt("minimumIdle"));
            c3p0Cfg.setInitialPoolSize(appCfg.getInt("minimumIdle"));
        });
        optionalHikariConfigToApplyMap.put("maximumPoolSize", (c3p0Cfg, appCfg) ->
            c3p0Cfg.setMaxPoolSize(appCfg.getInt("maximumPoolSize")));
        optionalHikariConfigToApplyMap.put("acquireIncrement", (c3p0Cfg, appCfg) ->
            c3p0Cfg.setAcquireIncrement(appCfg.getInt("acquireIncrement")));

        // Configuration of the different data sources
        final Map<String, DataSource> c3p0DataSourcePerNameMap = new HashMap<>();
        final Set<String> dbConfigurationNameSet = this.configuration.getConfig("voidframework.datasource").entrySet()
            .stream()
            .map(Map.Entry::getKey)
            .map(key -> {
                if (key.contains(".")) {
                    return key.substring(0, key.indexOf("."));
                } else {
                    return key;
                }
            })
            .collect(Collectors.toSet());

        if (dbConfigurationNameSet.isEmpty()) {
            throw new DataSourceException.NotConfigured();
        }

        for (final String dbConfigurationName : dbConfigurationNameSet) {

            final Config dbConfiguration = this.configuration.getConfig("voidframework.datasource." + dbConfigurationName);

            final ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
            comboPooledDataSource.setDataSourceName(dbConfigurationName);
            comboPooledDataSource.setJdbcUrl(dbConfiguration.getString("url"));
            comboPooledDataSource.setUser(dbConfiguration.getString("username"));
            comboPooledDataSource.setPassword(dbConfiguration.getString("password"));
            try {
                comboPooledDataSource.setDriverClass(dbConfiguration.getString("driver"));
            } catch (final PropertyVetoException exception) {
                throw new DataSourceException.DriverLoadFailure(dbConfiguration.getString("driver"), exception);
            }

            for (final Map.Entry<String, BiConsumer<ComboPooledDataSource, Config>> entrySet : optionalHikariConfigToApplyMap.entrySet()) {
                if (dbConfiguration.hasPath(entrySet.getKey())) {
                    entrySet.getValue().accept(comboPooledDataSource, dbConfiguration);
                }
            }

            c3p0DataSourcePerNameMap.put(dbConfigurationName, comboPooledDataSource);
        }

        // Create data source manager
        this.dataSourceManager = new DataSourceManager(c3p0DataSourcePerNameMap);
        return this.dataSourceManager;
    }
}
