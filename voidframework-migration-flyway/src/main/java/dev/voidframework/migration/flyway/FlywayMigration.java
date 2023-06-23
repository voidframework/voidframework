package dev.voidframework.migration.flyway;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.typesafe.config.Config;
import dev.voidframework.core.bindable.Bindable;
import dev.voidframework.core.lifecycle.LifeCycleStart;
import dev.voidframework.datasource.DataSourceManager;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Flyway migration.
 *
 * @since 1.0.0
 */
@Bindable
public final class FlywayMigration {

    private final Config configuration;
    private final Provider<DataSourceManager> dataSourceManagerProvider;

    /**
     * Build a new instance.
     *
     * @param configuration             The application configuration
     * @param dataSourceManagerProvider The data source manager provider
     * @since 1.0.0
     */
    @Inject
    public FlywayMigration(final Config configuration,
                           final Provider<DataSourceManager> dataSourceManagerProvider) {

        this.configuration = configuration;
        this.dataSourceManagerProvider = dataSourceManagerProvider;
    }

    /**
     * Migrates database using Flyway.
     *
     * @since 1.0.0
     */
    @LifeCycleStart(priority = 50)
    public void migrate() {

        final List<String> scriptLocationList = this.configuration.getStringList("voidframework.migration.flyway.scriptLocations")
            .stream()
            .filter(StringUtils::isNotEmpty)
            .toList();
        final List<String> callbackList = this.configuration.getStringList("voidframework.migration.flyway.callbacks")
            .stream()
            .filter(StringUtils::isNotEmpty)
            .toList();

        if (!scriptLocationList.isEmpty()) {
            // Configure Flyway
            final DataSource dataSource = dataSourceManagerProvider.get().getDataSource();
            final Flyway flyway = Flyway.configure()
                .loggers("slf4j")
                .callbacks(callbackList.toArray(String[]::new))
                .dataSource(dataSource)
                .encoding(StandardCharsets.UTF_8)
                .locations(scriptLocationList.toArray(String[]::new))
                .outOfOrder(this.configuration.getBoolean("voidframework.migration.flyway.outOfOrder"))
                .placeholderReplacement(this.configuration.getBoolean("voidframework.migration.flyway.placeholderReplacement"))
                .table(this.configuration.getString("voidframework.migration.flyway.historySchemaTable"))
                .tablespace(this.configuration.getString("voidframework.migration.flyway.historySchemaTablespace"))
                .load();

            // Migration!
            final MigrateResult result = flyway.migrate();

            // Validation
            if (result.migrationsExecuted > 0) {
                flyway.validate();
            }
        }
    }
}
