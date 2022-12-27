package dev.voidframework.core.exception;

/**
 * All exceptions thrown by the XML utility class are subclasses of {@code JsonException}.
 */
public class XmlException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     */
    protected XmlException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Exception indicates that conversion to a XML string just fail.
     */
    public static class ToStringConversionFailure extends JsonException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public ToStringConversionFailure(final Throwable cause) {

            super("To XML string conversion failure", cause);
        }
    }

    /**
     * Exception indicates that conversion to XML just fail.
     */
    public static class ToXmlConversionFailure extends XmlException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public ToXmlConversionFailure(final Throwable cause) {

            super("To XML conversion failure", cause);
        }
    }

    /**
     * Exception indicates that conversion from XML just fail.
     */
    public static class FromXmlConversionFailure extends XmlException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public FromXmlConversionFailure(final Throwable cause) {

            super("From XML conversion failure", cause);
        }
    }
}
