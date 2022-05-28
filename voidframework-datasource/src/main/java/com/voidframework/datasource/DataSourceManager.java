package com.voidframework.datasource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Map;

/**
 * Data source manager.
 */
public final class DataSourceManager {

    private final Map<String, DataSource> dataSourcePerNameMap;

    /**
     * Build a new instance.
     *
     * @param dataSourcePerNameMap Handled data sources
     */
    public DataSourceManager(final Map<String, DataSource> dataSourcePerNameMap) {
        this.dataSourcePerNameMap = dataSourcePerNameMap;
    }

    /**
     * Retrieve a connection from the default data source.
     *
     * @return A connection to the data source
     * @throws SQLException        If a database access error occurs
     * @throws SQLTimeoutException If a timeout occur during database access
     */
    public Connection getConnection() throws SQLException {
        return this.dataSourcePerNameMap.get("default").getConnection();
    }

    /**
     * Retrieve a connection from a specific data source.
     *
     * @param dataSourceName The datasource name
     * @return A connection to the data source
     * @throws SQLException        If a database access error occurs
     * @throws SQLTimeoutException If a timeout occur during database access
     */
    public Connection getConnection(final String dataSourceName) throws SQLException {
        final DataSource dataSource = this.dataSourcePerNameMap.get(dataSourceName);
        return dataSource != null ? dataSource.getConnection() : null;
    }

    /**
     * Retrieve the default data source.
     *
     * @return The data source
     */
    public DataSource getDataSource() {
        return this.dataSourcePerNameMap.get("default");
    }

    /**
     * Retrieve a specific data source.
     *
     * @return The data source
     */
    public DataSource getDataSource(final String dataSourceName) {
        return this.dataSourcePerNameMap.get(dataSourceName);
    }
}
