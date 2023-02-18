package dev.voidframework.template.exception;

/**
 * All exceptions related to template rendering errors are subclasses of {@code TemplateException}.
 *
 * @since 1.0.0
 */
public class TemplateException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     * @since 1.0.0
     */
    protected TemplateException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @since 1.0.0
     */
    protected TemplateException(final String message) {

        this(message, null);
    }

    /**
     * Exception indicates that data model was not provided.
     *
     * @since 1.0.0
     */
    public static class DataModelNotProvided extends TemplateException {

        /**
         * Build a new instance.
         *
         * @since 1.0.0
         */
        public DataModelNotProvided() {

            super("Data model was not provided");
        }
    }

    /**
     * Exception indicates that rendering process failure.
     *
     * @since 1.0.0
     */
    public static class RenderingFailure extends TemplateException {

        private final String templateName;
        private final int lineNumber;

        /**
         * Build a new instance.
         *
         * @param templateName The template name
         * @param lineNumber   The line where the error occur
         * @param cause        The cause
         * @since 1.0.0
         */
        public RenderingFailure(final String templateName, final int lineNumber, final Throwable cause) {

            super("Can't render template", cause);
            this.templateName = templateName;
            this.lineNumber = lineNumber;
        }

        /**
         * Gets the template name.
         *
         * @return The template name
         * @since 1.0.0
         */
        public String getTemplateName() {

            return this.templateName;
        }

        /**
         * Gets the line number.
         *
         * @return The line number
         * @since 1.0.0
         */
        public int getLineNumber() {

            return this.lineNumber;
        }
    }

    /**
     * Exception indicates that an error occur during the initialization of the template engine.
     *
     * @since 1.0.0
     */
    public static class TemplateEngineInitFailure extends TemplateException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         * @since 1.0.0
         */
        public TemplateEngineInitFailure(final Throwable cause) {

            super("Can't initialize the template engine", cause);
        }
    }

    /**
     * Exception indicates that template engine wasn't found.
     *
     * @since 1.0.0
     */
    public static class NoTemplateEngine extends TemplateException {

        /**
         * Build a new instance.
         *
         * @since 1.0.0
         */
        public NoTemplateEngine() {

            super("No template engine found, check that you have activated an implementation of the template engine");
        }
    }
}
