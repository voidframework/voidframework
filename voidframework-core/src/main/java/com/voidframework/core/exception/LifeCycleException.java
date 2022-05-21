package com.voidframework.core.exception;

/**
 * All exceptions thrown by the life cycle features are subclasses of {@code LifeCycleException}.
 */
public class LifeCycleException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param message The detail message
     * @param cause   The cause
     */
    protected LifeCycleException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception indicates that method invocation failed miserably.
     */
    public static class InvocationFailure extends LifeCycleException {

        /**
         * Build a new instance.
         *
         * @param cause The cause
         */
        public InvocationFailure(final String className, final String methodName, final Throwable cause) {
            super("Life cycle invocation failure for " + className + "::" + methodName, cause);
        }
    }
}
