package dev.voidframework.datasource.hikaricp.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.voidframework.datasource.DataSourceManager;
import dev.voidframework.exception.DataSourceException;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * HikariCP data source manager provider.
 */
@Singleton
public class HikariCpDataSourceManagerProvider implements Provider<DataSourceManager> {

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
        optionalHikariConfigToApplyMap.put("cachePrepStmts", (hikariCfg, appCfg) ->
            hikariCfg.addDataSourceProperty("cachePrepStmts", appCfg.getString("cachePrepStmts")));
        optionalHikariConfigToApplyMap.put("prepStmtCacheSize", (hikariCfg, appCfg) ->
            hikariCfg.addDataSourceProperty("prepStmtCacheSize", appCfg.getString("prepStmtCacheSize")));
        optionalHikariConfigToApplyMap.put("prepStmtCacheSqlLimit", (hikariCfg, appCfg) ->
            hikariCfg.addDataSourceProperty("prepStmtCacheSqlLimit", appCfg.getString("prepStmtCacheSqlLimit")));
        optionalHikariConfigToApplyMap.put("connectionTimeout", (hikariCfg, appCfg) ->
            hikariCfg.setConnectionTimeout(appCfg.getInt("connectionTimeout")));
        optionalHikariConfigToApplyMap.put("idleTimeout",
            (hikariCfg, appCfg) -> hikariCfg.setIdleTimeout(appCfg.getInt("idleTimeout")));
        optionalHikariConfigToApplyMap.put("keepaliveTime",
            (hikariCfg, appCfg) -> hikariCfg.setKeepaliveTime(appCfg.getInt("keepaliveTime")));
        optionalHikariConfigToApplyMap.put("connectionInitSql", (hikariCfg, appCfg) ->
            hikariCfg.setConnectionInitSql(appCfg.getString("connectionInitSql")));
        optionalHikariConfigToApplyMap.put("connectionTestQuery", (hikariCfg, appCfg) ->
            hikariCfg.setConnectionTestQuery(appCfg.getString("connectionTestQuery")));
        optionalHikariConfigToApplyMap.put("autoCommit", (hikariCfg, appCfg) ->
            hikariCfg.setAutoCommit(appCfg.getBoolean("autoCommit")));
        optionalHikariConfigToApplyMap.put("minimumIdle", (hikariCfg, appCfg) ->
            hikariCfg.setMinimumIdle(appCfg.getInt("minimumIdle")));
        optionalHikariConfigToApplyMap.put("maximumPoolSize", (hikariCfg, appCfg) ->
            hikariCfg.setMaximumPoolSize(appCfg.getInt("maximumPoolSize")));

        // Configuration of the different data sources
        final Map<String, DataSource> hikariDataSourcePerNameMap = new HashMap<>();
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

            final HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setPoolName(dbConfigurationName);
            hikariConfig.setDriverClassName(dbConfiguration.getString("driver"));
            hikariConfig.setJdbcUrl(dbConfiguration.getString("url"));
            hikariConfig.setUsername(dbConfiguration.getString("username"));
            hikariConfig.setPassword(dbConfiguration.getString("password"));

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
