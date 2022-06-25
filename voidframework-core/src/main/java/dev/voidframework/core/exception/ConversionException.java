package dev.voidframework.core.exception;

/**
 * All exceptions thrown by the conversion features are subclasses of {@code ConversionException}.
 */
public class ConversionException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected ConversionException(final String message) {

        super(message, null);
    }

    /**
     * Exception indicates that provided converter is already registered.
     */
    public static class ConverterAlreadyRegistered extends ConversionException {

        /**
         * Build a new instance.
         *
         * @param sourceTypeClass The source type class
         * @param targetTypeClass The target type class
         */
        public ConverterAlreadyRegistered(final Class<?> sourceTypeClass,
                                          final Class<?> targetTypeClass) {

            super("Converter<source=" + sourceTypeClass.getName() + ", target=" + targetTypeClass.getName() + "> already registered");
        }
    }

    /**
     * Exception indicates that requested converter does not exist.
     */
    public static class ConverterDoesNotExist extends ConversionException {

        /**
         * Build a new instance.
         *
         * @param sourceTypeClass The source type class
         * @param targetTypeClass The target type class
         */
        public ConverterDoesNotExist(final Class<?> sourceTypeClass,
                                     final Class<?> targetTypeClass) {

            super("Converter<source=" + sourceTypeClass.getName() + ", target=" + targetTypeClass.getName() + "> does not exist");
        }
    }

    /**
     * Exception indicates that something goes wrong during the converter initialization.
     */
    public static class InvalidConverter extends ConversionException {

        /**
         * Build a new instance.
         *
         * @param converterName Name of the converter
         * @param errorMessage  The error message
         */
        public InvalidConverter(final String converterName, final String errorMessage) {

            super("Converter '" + converterName + "' is invalid: " + errorMessage);
        }
    }
}
