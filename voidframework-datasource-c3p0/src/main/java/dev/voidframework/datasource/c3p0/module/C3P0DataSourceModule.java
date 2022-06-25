package dev.voidframework.datasource.c3p0.module;

import com.google.inject.AbstractModule;
import dev.voidframework.datasource.DataSourceManager;

/**
 * C3P0 data source module.
 */
public class C3P0DataSourceModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(DataSourceManager.class).toProvider(C3P0DataSourceManagerProvider.class);
    }
}
