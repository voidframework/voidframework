package dev.voidframework.datasource.hikaricp.module;

import com.google.inject.AbstractModule;
import dev.voidframework.datasource.DataSourceManager;

/**
 * HikariCP data source module.
 */
public class HikariCpDataSourceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DataSourceManager.class).toProvider(HikariCpDataSourceManagerProvider.class);
    }
}
