package com.voidframework.datasource.module;

import com.google.inject.AbstractModule;
import com.voidframework.datasource.DataSourceManager;

public class DataSourceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DataSourceManager.class).toProvider(DataSourceManagerProvider.class);
    }
}
