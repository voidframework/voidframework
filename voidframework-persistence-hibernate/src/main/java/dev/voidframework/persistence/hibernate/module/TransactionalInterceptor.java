package dev.voidframework.persistence.hibernate.module;

import com.google.inject.Inject;
import dev.voidframework.core.helper.ProxyDetector;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TransactionRequiredException;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Intercepts method call when annotation {@link Transactional} is used.
 */
public class TransactionalInterceptor implements MethodInterceptor {

    private EntityManagerProvider entityManagerProvider;

    /**
     * Sets the entity manager provider.
     *
     * @param entityManagerProvider The entity manager
     */
    @Inject
    public void setDataSourceManagerProvider(final EntityManagerProvider entityManagerProvider) {

        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {

        // Retrieves the transaction configuration
        Transactional transactionalAnnotation = methodInvocation.getMethod().getAnnotation(Transactional.class);
        if (transactionalAnnotation == null) {
            transactionalAnnotation = methodInvocation.getThis().getClass().getAnnotation(Transactional.class);
        }

        // Create a new EntityManager for this current thread (must be done once)
        if (this.entityManagerProvider.isEntityManagerMustBeInitialized() || transactionalAnnotation.value() == Transactional.TxType.REQUIRES_NEW) {
            this.entityManagerProvider.initializeNewEntityFactoryManager();
        }

        // Retrieve transaction
        final EntityManager entityManager = this.entityManagerProvider.get();
        final EntityTransaction transaction = entityManager.getTransaction();

        // Checks transaction context
        final boolean isTransactionActive = transaction.isActive();

        if (transactionalAnnotation.value() == Transactional.TxType.NOT_SUPPORTED) {
            // The configuration indicates that current method must run outside a transaction context
            if (isTransactionActive) {
                this.entityManagerProvider.initializeNewEntityFactoryManager();
            }
            return methodInvocation.proceed();
        }

        if (isTransactionActive && transactionalAnnotation.value() == Transactional.TxType.NEVER) {
            // The configuration indicates that a transaction must not exist
            // This is not the case here, so an exception will be thrown
            throw new InvalidTransactionException("%s::%s called inside a transaction context".formatted(
                ProxyDetector.isProxy(methodInvocation.getThis())
                    ? methodInvocation.getThis().getClass().getSuperclass().getName()
                    : methodInvocation.getThis().getClass().getName(),
                methodInvocation.getMethod().getName()));
        } else if (transactionalAnnotation.value() == Transactional.TxType.NEVER) {
            return methodInvocation.proceed();
        }

        if (isTransactionActive || transactionalAnnotation.value() == Transactional.TxType.SUPPORTS) {
            // A transaction exist or the configuration indicates than method
            // can be call with or without an existing transaction
            return methodInvocation.proceed();
        } else if (transactionalAnnotation.value() == Transactional.TxType.MANDATORY) {
            // The configuration indicates that a transaction must already exist
            // This is not the case here, so an exception will be thrown
            throw new TransactionRequiredException("%s::%s called outside a transaction context".formatted(
                ProxyDetector.isProxy(methodInvocation.getThis())
                    ? methodInvocation.getThis().getClass().getSuperclass().getName()
                    : methodInvocation.getThis().getClass().getName(),
                methodInvocation.getMethod().getName()));
        }

        // Creates a new transaction and then executes the method. If something goes
        // wrong, and depending on the configuration, a rollback will be performed
        try {
            transaction.begin();
            final Object result = methodInvocation.proceed();
            transaction.commit();
            return result;
        } catch (final Throwable throwable) {
            if (this.hasToRollback(transactionalAnnotation, throwable.getClass())) {
                transaction.rollback();
            } else {
                transaction.commit();
            }
            throw throwable;
        } finally {
            this.entityManagerProvider.destroyLatestEntityManager();
        }
    }

    /**
     * Determines whether a rollback should be performed.
     *
     * @param transactionalAnnotation The current transaction annotation
     * @param throwableClass          The caught throwable class
     * @return {@code true} if a rollback should be performed, otherwise {@code false}
     */
    private boolean hasToRollback(final Transactional transactionalAnnotation, final Class<?> throwableClass) {

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
