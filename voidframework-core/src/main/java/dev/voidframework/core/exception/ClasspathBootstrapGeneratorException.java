package dev.voidframework.core.exception;

/**
 * All exceptions thrown by the "classpath.bootstrap" generator
 * are subclasses of {@code ClasspathBootstrapGeneratorException}.
 */
public class ClasspathBootstrapGeneratorException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected ClasspathBootstrapGeneratorException(final String message) {

        super(message, null);
    }

    /**
     * Exception indicates that provided program argument is missing.
     */
    public static class MissingProgramArgument extends ClasspathBootstrapGeneratorException {

        /**
         * Build a new instance.
         *
         * @param message The message
         */
        public MissingProgramArgument(final String message) {

            super("Missing argument: " + message);
        }
    }
}
