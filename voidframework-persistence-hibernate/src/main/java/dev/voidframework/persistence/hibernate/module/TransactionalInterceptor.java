package dev.voidframework.persistence.hibernate.module;

import com.google.inject.Inject;
import dev.voidframework.core.utils.ProxyDetectorUtils;
import dev.voidframework.persistence.AbstractTransactionalInterceptor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TransactionRequiredException;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Intercepts method call when annotation {@link Transactional} is used.
 *
 * @since 1.0.0
 */
public class TransactionalInterceptor extends AbstractTransactionalInterceptor {

    private EntityManagerProvider entityManagerProvider;

    /**
     * Sets the entity manager provider.
     *
     * @param entityManagerProvider The entity manager provider
     * @since 1.0.0
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
                ProxyDetectorUtils.isProxy(methodInvocation.getThis())
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
                ProxyDetectorUtils.isProxy(methodInvocation.getThis())
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
}
