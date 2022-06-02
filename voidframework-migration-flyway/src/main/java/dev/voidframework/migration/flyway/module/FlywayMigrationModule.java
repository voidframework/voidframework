package dev.voidframework.migration.flyway.module;

import com.google.inject.AbstractModule;
import dev.voidframework.migration.flyway.FlywayMigration;

/**
 * Flyway migration module.
 */
public class FlywayMigrationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FlywayMigration.class).asEagerSingleton();
    }
}
