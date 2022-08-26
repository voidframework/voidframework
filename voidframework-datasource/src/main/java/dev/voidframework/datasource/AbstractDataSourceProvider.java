package dev.voidframework.datasource;

import com.google.inject.Provider;
import com.typesafe.config.Config;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.exception.DataSourceException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This abstraction provides common methods for all {@code DataSourceManager} providers.
 */
public abstract class AbstractDataSourceProvider implements Provider<DataSourceManager> {

    protected static final String PREFIX_CONFIGURATION_KEY_DATASOURCE = "voidframework.datasource.";
    private static final String CONFIGURATION_KEY_DATASOURCE = "voidframework.datasource";

    /**
     * Retrieves all DataSource configuration name.
     *
     * @param configuration The application configuration
     * @return DataSource configuration names
     */
    protected Set<String> retrieveDataSourceConfigurationNames(final Config configuration) {

        final Set<String> dbConfigurationNameSet = configuration.getConfig(CONFIGURATION_KEY_DATASOURCE).entrySet()
            .stream()
            .map(Map.Entry::getKey)
            .map(key -> {
                if (key.contains(StringConstants.DOT)) {
                    return key.substring(0, key.indexOf(StringConstants.DOT));
                } else {
                    return key;
                }
            })
            .collect(Collectors.toSet());

        if (dbConfigurationNameSet.isEmpty()) {
            throw new DataSourceException.NotConfigured();
        }

        return dbConfigurationNameSet;
    }
}
