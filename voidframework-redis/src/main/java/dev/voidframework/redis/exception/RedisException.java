package dev.voidframework.redis.exception;

/**
 * All exceptions related to Redis feature are subclasses of {@code RedisException}.
 */
public class RedisException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     */
    protected RedisException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected RedisException(final String message) {

        this(message, null);
    }

    /**
     * Exception indicates that configuration is invalid.
     */
    public static class InvalidConfiguration extends RedisException {

        /**
         * Build a new instance.
         *
         * @param configurationKey The configuration key
         */
        public InvalidConfiguration(final String configurationKey) {

            super("Redis configuration '" + configurationKey + "' is invalid");
        }
    }

    /**
     * Exception indicates that something goes wrong during callable call.
     */
    public static class CallableFailure extends RedisException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public CallableFailure(final Throwable cause) {

            super("CallableFailure: " + cause.getMessage(), cause);
        }
    }
}
