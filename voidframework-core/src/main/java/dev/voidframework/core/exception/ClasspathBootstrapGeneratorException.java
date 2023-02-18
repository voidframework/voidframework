package dev.voidframework.core.exception;

/**
 * All exceptions thrown by the "classpath.bootstrap" generator
 * are subclasses of {@code ClasspathBootstrapGeneratorException}.
 *
 * @since 1.2.0
 */
public class ClasspathBootstrapGeneratorException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.2.0
     */
    protected ClasspathBootstrapGeneratorException(final String message) {

        super(message, null);
    }

    /**
     * Exception indicates that provided program argument is missing.
     *
     * @since 1.2.0
     */
    public static class MissingProgramArgument extends ClasspathBootstrapGeneratorException {

        /**
         * Build a new instance.
         *
         * @param message The message
         * @since 1.2.0
         */
        public MissingProgramArgument(final String message) {

            super("Missing argument: " + message);
        }
    }
}
