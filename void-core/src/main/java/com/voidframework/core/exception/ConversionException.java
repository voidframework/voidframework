package com.voidframework.core.exception;

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
     * Exception indicates that requested converter does not exists.
     */
    public static class ConverterDoesNotExists extends ConversionException {

        /**
         * Build a new instance.
         *
         * @param sourceTypeClass The source type class
         * @param targetTypeClass The target type class
         */
        public ConverterDoesNotExists(final Class<?> sourceTypeClass,
                                      final Class<?> targetTypeClass) {
            super("Converter<source=" + sourceTypeClass + ", target=" + targetTypeClass + "> does not exist");
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
