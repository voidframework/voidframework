package dev.voidframework.bucket4j.exception;

/**
 * All exceptions thrown by "bucket-token" (Bucket4J) module are subclasses of {@code BucketTokenException}.
 *
 * @since 1.9.0
 */
public class BucketTokenException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.9.0
     */
    protected BucketTokenException(final String message) {

        this(message, null);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     * @since 1.9.0
     */
    protected BucketTokenException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Exception indicates that requested Bucket does not exist.
     *
     * @since 1.9.0
     */
    public static class BucketDoesNotExist extends BucketTokenException {

        /**
         * Build a new instance.
         *
         * @since 1.9.0
         */
        public BucketDoesNotExist(final String bucketName) {

            super("Bucket '" + bucketName + "' does not exist");
        }
    }

    /**
     * Exception indicates that specified refill strategy is unknown.
     *
     * @since 1.9.0
     */
    public static class UnknownRefillStrategy extends BucketTokenException {

        /**
         * Build a new instance.
         *
         * @since 1.9.0
         */
        public UnknownRefillStrategy(final String refillStrategy) {

            super("Unknown refill strategy '" + refillStrategy + "'. Accepted values are: GREEDY, INTERVALLY, or INTERVALLY_ALIGNED");
        }
    }

    /**
     * Exception indicates that Bucket does not have enough
     * tokens to enter protected method.
     *
     * @since 1.9.0
     */
    public static class NoEnoughTokensAvailable extends BucketTokenException {

        private final String bucketName;

        /**
         * Build a new instance.
         *
         * @since 1.9.0
         */
        public NoEnoughTokensAvailable(final String bucketName) {

            super("Bucket '" + bucketName + "' does not have enough tokens available");
            this.bucketName = bucketName;
        }

        /**
         * Gets the bucket name.
         *
         * @return The bucket name
         * @since 1.9.0
         */
        public String getBucketName() {

            return this.bucketName;
        }
    }
}
