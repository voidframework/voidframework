package dev.voidframework.exception;

/**
 * All exceptions thrown by the Data Source feature are subclasses of {@code DataSourceException}.
 */
public class DataSourceException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected DataSourceException(final String message) {
        this(message, null);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     */
    protected DataSourceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception indicates that data source configuration is missing.
     */
    public static class NotConfigured extends DataSourceException {

        /**
         * Build a new instance.
         */
        public NotConfigured() {
            super("DataSource is not configured");
        }
    }
}
