package com.voidframework.migration.flyway.module;

import com.google.inject.AbstractModule;
import com.voidframework.migration.flyway.FlywayMigration;

/**
 * Flyway migration module.
 */
public class FlywayMigrationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FlywayMigration.class).asEagerSingleton();
    }
}
