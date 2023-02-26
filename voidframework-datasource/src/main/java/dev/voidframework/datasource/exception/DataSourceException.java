package dev.voidframework.datasource.exception;

/**
 * All exceptions thrown by the Data Source feature are subclasses of {@code DataSourceException}.
 *
 * @since 1.4.0
 */
public class DataSourceException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.4.0
     */
    protected DataSourceException(final String message) {

        this(message, null);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     * @since 1.4.0
     */
    protected DataSourceException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Exception indicates that driver can't be loaded.
     *
     * @since 1.4.0
     */
    public static class DriverLoadFailure extends DataSourceException {

        /**
         * Build a new instance.
         *
         * @param driverClassName The driver class name
         * @param cause           The cause
         * @since 1.4.0
         */
        public DriverLoadFailure(final String driverClassName, final Throwable cause) {

            super("Failed to load driver class '" + driverClassName + "'", cause);
        }
    }

    /**
     * Exception indicates that data source configuration is missing.
     *
     * @since 1.4.0
     */
    public static class NotConfigured extends DataSourceException {

        /**
         * Build a new instance.
         *
         * @since 1.4.0
         */
        public NotConfigured() {

            super("DataSource is not configured");
        }
    }
}
