package dev.voidframework.core.exception;

/**
 * All exceptions thrown by the life cycle features are subclasses of {@code LifeCycleException}.
 *
 * @since 1.0.0
 */
public class LifeCycleException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     * @since 1.0.0
     */
    protected LifeCycleException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Exception indicates that method invocation failed miserably.
     *
     * @since 1.0.0
     */
    public static class InvocationFailure extends LifeCycleException {

        /**
         * Build a new instance.
         *
         * @param className  The class name
         * @param methodName The invoked method name
         * @param cause      The cause
         * @since 1.0.0
         */
        public InvocationFailure(final String className, final String methodName, final Throwable cause) {

            super("Life cycle invocation failure for " + className + "::" + methodName, cause);
        }
    }
}
