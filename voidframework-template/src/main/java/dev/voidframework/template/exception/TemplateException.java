package dev.voidframework.template.exception;

/**
 * All exceptions related to template rendering errors are subclasses of {@code TemplateException}.
 */
public class TemplateException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     */
    protected TemplateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected TemplateException(final String message) {
        this(message, null);
    }

    /**
     * Exception indicates that data model was not provided.
     */
    public static class DataModelNotProvided extends TemplateException {

        /**
         * Build a new instance.
         */
        public DataModelNotProvided() {
            super("Data model was not provided");
        }
    }

    /**
     * Exception indicates that rendering process failure.
     */
    public static class RenderingFailure extends TemplateException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public RenderingFailure(final Throwable cause) {
            super("Can't render template", cause);
        }
    }

    /**
     * Exception indicates that an error occur during the initialization of the template engine.
     */
    public static class TemplateEngineInitFailure extends TemplateException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public TemplateEngineInitFailure(final Throwable cause) {
            super("Can't initialize the template engine", cause);
        }
    }
}
