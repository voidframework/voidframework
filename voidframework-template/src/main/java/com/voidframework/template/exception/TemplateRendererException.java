package com.voidframework.template.exception;

/**
 * All exceptions related to template rendering errors are subclasses of {@code TemplateRendererException}.
 */
public class TemplateRendererException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The root cause
     */
    protected TemplateRendererException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Build a new instance.
     *
     * @param message The detail message
     */
    protected TemplateRendererException(final String message) {
        this(message, null);
    }

    /**
     * Exception indicates that data model was not provided.
     */
    public static class DataModelNotProvided extends TemplateRendererException {

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
    public static class RenderingFailure extends TemplateRendererException {

        /**
         * Build a new instance.
         */
        public RenderingFailure(final Throwable cause) {
            super("Can't render template", cause);
        }
    }
}
