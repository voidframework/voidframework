package dev.voidframework.datasource.utils;

import com.typesafe.config.Config;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.datasource.exception.DataSourceException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data source utility methods.
 *
 * @since 1.4.0
 */
public final class DataSourceUtils {

    /**
     * Default constructor.
     *
     * @since 1.4.0
     */
    private DataSourceUtils() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Returns all existing data source names.
     *
     * @param configuration The application configuration
     * @return Existing data source names
     * @throws DataSourceException.NotConfigured If data source was not found in the configuration
     * @since 1.4.0
     */
    public static Set<String> getAllDataSourceNames(final Config configuration) {

        if (!configuration.hasPathOrNull("voidframework.datasource")) {
            throw new DataSourceException.NotConfigured();
        }

        return configuration.getConfig("voidframework.datasource").entrySet()
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
    }
}
