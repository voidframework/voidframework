package dev.voidframework.core.exception;

/**
 * All exceptions thrown by the conversion features are subclasses of {@code ConversionException}.
 *
 * @since 1.0.0
 */
public class ConversionException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.0.0
     */
    protected ConversionException(final String message) {

        super(message, null);
    }

    /**
     * Exception indicates that provided converter is already registered.
     *
     * @since 1.0.0
     */
    public static class ConverterAlreadyRegistered extends ConversionException {

        /**
         * Build a new instance.
         *
         * @param sourceTypeClass The source type class
         * @param targetTypeClass The target type class
         * @since 1.0.0
         */
        public ConverterAlreadyRegistered(final Class<?> sourceTypeClass,
                                          final Class<?> targetTypeClass) {

            super("Converter<source=" + sourceTypeClass.getName() + ", target=" + targetTypeClass.getName() + "> already registered");
        }
    }

    /**
     * Exception indicates that requested converter does not exist.
     *
     * @since 1.0.0
     */
    public static class ConverterDoesNotExist extends ConversionException {

        /**
         * Build a new instance.
         *
         * @param sourceTypeClass The source type class
         * @param targetTypeClass The target type class
         * @since 1.0.0
         */
        public ConverterDoesNotExist(final Class<?> sourceTypeClass,
                                     final Class<?> targetTypeClass) {

            super("Converter<source=" + sourceTypeClass.getName() + ", target=" + targetTypeClass.getName() + "> does not exist");
        }
    }

    /**
     * Exception indicates that something goes wrong during the converter initialization.
     *
     * @since 1.0.0
     */
    public static class InvalidConverter extends ConversionException {

        /**
         * Build a new instance.
         *
         * @param converterName Name of the converter
         * @param errorMessage  The error message
         * @since 1.0.0
         */
        public InvalidConverter(final String converterName, final String errorMessage) {

            super("Converter '" + converterName + "' is invalid: " + errorMessage);
        }
    }
}
