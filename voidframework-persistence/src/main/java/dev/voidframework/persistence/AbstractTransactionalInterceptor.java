package dev.voidframework.persistence;

import jakarta.transaction.Transactional;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * Provides base methods for all transactional interceptors.
 *
 * @since 1.4.0
 */
public abstract class AbstractTransactionalInterceptor implements MethodInterceptor {

    /**
     * Determines whether a rollback should be performed.
     *
     * @param transactionalAnnotation The current transaction annotation
     * @param throwableClass          The caught throwable class
     * @return {@code true} if a rollback should be performed, otherwise {@code false}
     * @since 1.4.0
     */
    protected boolean hasToRollback(final Transactional transactionalAnnotation, final Class<?> throwableClass) {

        // "dontRollbackOn" always takes precedence
        for (final Class<?> dontRollbackOn : transactionalAnnotation.dontRollbackOn()) {
            if (dontRollbackOn == throwableClass) {
                return false;
            }
        }

        // If the list is empty, you simply need to rollback
        if (transactionalAnnotation.rollbackOn().length == 0
            && RuntimeException.class.isAssignableFrom(throwableClass)) {
            return true;
        }

        // But on the other hand, if it is not, rollback is only allowed
        // if the exception class is present in the list
        for (final Class<?> rollbackOn : transactionalAnnotation.rollbackOn()) {
            if (rollbackOn == throwableClass) {
                return true;
            }
        }

        return false;
    }
}
