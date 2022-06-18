package dev.voidframework.web.exception;

/**
 * All exceptions related to the Filter feature are subclasses of {@code FilterException}.
 */
public class FilterException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     */
    protected FilterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected FilterException(final String message) {
        this(message, null);
    }

    /**
     * Exception indicates that Filter class can't be loaded.
     */
    public static class LoadFailure extends FilterException {

        /**
         * Build a new instance.
         *
         * @param filterClassName The Filter class name
         */
        public LoadFailure(final String filterClassName) {
            super("Can't load Filter '" + filterClassName + "'");
        }
    }

    /**
     * Exception indicates that an overflow occur during the filter chain processing.
     */
    public static class Overflow extends FilterException {

        /**
         * Build a new instance.
         *
         * @param currentIndex   The current index position
         * @param filterListSize The filter list size
         */
        public Overflow(final int currentIndex, final int filterListSize) {
            super("Filter chain overflow detected! Current index ("
                + currentIndex
                + ") is greater or equal to filter list size ("
                + filterListSize + ")");
        }
    }
}
