package dev.voidframework.core.exception;

/**
 * All exceptions thrown by the YAML utility class are subclasses of {@code YamlException}.
 *
 * @since 1.2.0
 */
public class YamlException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     * @since 1.2.0
     */
    protected YamlException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Exception indicates that conversion to a JSON string just fail.
     *
     * @since 1.2.0
     */
    public static class ToStringConversionFailure extends JsonException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         * @since 1.2.0
         */
        public ToStringConversionFailure(final Throwable cause) {

            super("To YAML string conversion failure", cause);
        }
    }

    /**
     * Exception indicates that conversion to YAML just fail.
     *
     * @since 1.2.0
     */
    public static class ToYamlConversionFailure extends YamlException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         * @since 1.2.0
         */
        public ToYamlConversionFailure(final Throwable cause) {

            super("To YAML conversion failure", cause);
        }
    }

    /**
     * Exception indicates that conversion from YAML just fail.
     *
     * @since 1.2.0
     */
    public static class FromYamlConversionFailure extends YamlException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         * @since 1.2.0
         */
        public FromYamlConversionFailure(final Throwable cause) {

            super("From YAML conversion failure", cause);
        }
    }
}
