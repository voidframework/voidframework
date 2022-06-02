package dev.voidframework.datasource.module;

import com.google.inject.AbstractModule;
import dev.voidframework.datasource.DataSourceManager;

public class DataSourceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DataSourceManager.class).toProvider(dev.voidframework.datasource.module.DataSourceManagerProvider.class);
    }
}
