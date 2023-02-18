package dev.voidframework.web.exception;

/**
 * All exceptions related to the Filter feature are subclasses of {@code FilterException}.
 *
 * @since 1.0.0
 */
public class FilterException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     * @since 1.0.0
     */
    protected FilterException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.0.0
     */
    protected FilterException(final String message) {

        this(message, null);
    }

    /**
     * Exception indicates that Filter class can't be loaded.
     *
     * @since 1.0.0
     */
    public static class LoadFailure extends FilterException {

        /**
         * Build a new instance.
         *
         * @param filterClassName The Filter class name
         * @since 1.0.0
         */
        public LoadFailure(final String filterClassName) {

            super("Can't load Filter '" + filterClassName + "'");
        }
    }

    /**
     * Exception indicates that an overflow occur during the filter chain processing.
     *
     * @since 1.0.0
     */
    public static class Overflow extends FilterException {

        /**
         * Build a new instance.
         *
         * @param currentIndex   The current index position
         * @param filterListSize The filter list size
         * @since 1.0.0
         */
        public Overflow(final int currentIndex, final int filterListSize) {

            super(
                "Filter chain overflow detected! Current index ("
                    + currentIndex
                    + ") is greater or equal to filter list size ("
                    + filterListSize + ")");
        }
    }
}
