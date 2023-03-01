package dev.voidframework.persistence.hibernate.module;

import com.google.inject.Inject;
import dev.voidframework.core.utils.ProxyDetectorUtils;
import dev.voidframework.persistence.AbstractTransactionalInterceptor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.TransactionRequiredException;
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

        boolean isInitiator = false;

        // Retrieves the transaction configuration
        Transactional transactionalAnnotation = methodInvocation.getMethod().getAnnotation(Transactional.class);
        if (transactionalAnnotation == null) {
            transactionalAnnotation = methodInvocation.getThis().getClass().getAnnotation(Transactional.class);
        }

        // Creates a new EntityManager for this current thread (must be done once)
        if (this.entityManagerProvider.isEntityManagerMustBeInitialized()) {
            this.entityManagerProvider.initializeNewEntityFactoryManager();
            isInitiator = true;
        }

        // Retrieves current entity transaction
        final EntityManager entityManager = this.entityManagerProvider.get();
        final EntityTransaction transaction = entityManager.getTransaction();

        try {
            return switch (transactionalAnnotation.value()) {
                case MANDATORY:
                    if (!transaction.isActive()) {
                        // The configuration indicates that a transaction must already exist
                        // This is not the case here, so an exception will be thrown
                        throw new TransactionRequiredException("%s::%s called outside a transaction context".formatted(
                            ProxyDetectorUtils.isProxy(methodInvocation.getThis())
                                ? methodInvocation.getThis().getClass().getSuperclass().getName()
                                : methodInvocation.getThis().getClass().getName(),
                            methodInvocation.getMethod().getName()));
                    }
                    yield methodInvocation.proceed();
                case NEVER:
                    if (transaction.isActive()) {
                        // The configuration indicates that a transaction must not exist
                        // This is not the case here, so an exception will be thrown
                        throw new InvalidTransactionException("%s::%s called inside a transaction context".formatted(
                            ProxyDetectorUtils.isProxy(methodInvocation.getThis())
                                ? methodInvocation.getThis().getClass().getSuperclass().getName()
                                : methodInvocation.getThis().getClass().getName(),
                            methodInvocation.getMethod().getName()));
                    }
                    yield methodInvocation.proceed();
                case NOT_SUPPORTED:
                    // The configuration indicates that current method must run outside a transaction context
                    if (transaction.isActive()) {
                        isInitiator = true;
                        this.entityManagerProvider.initializeNewEntityFactoryManager();
                    }
                    yield methodInvocation.proceed();
                case REQUIRED:
                    if (!transaction.isActive()) {
                        yield this.proceedInTransaction(methodInvocation, transaction, transactionalAnnotation);
                    }
                    yield methodInvocation.proceed();
                case REQUIRES_NEW:
                    // A new transaction must be created in any case
                    if (!isInitiator) {
                        isInitiator = true;

                        this.entityManagerProvider.initializeNewEntityFactoryManager();
                        final EntityTransaction newEntityTransaction = this.entityManagerProvider.get().getTransaction();

                        yield this.proceedInTransaction(methodInvocation, newEntityTransaction, transactionalAnnotation);
                    } else {
                        yield this.proceedInTransaction(methodInvocation, transaction, transactionalAnnotation);
                    }
                case SUPPORTS:
                    // A transaction exist or the configuration indicates than method
                    // can be call with or without an existing transaction
                    yield methodInvocation.proceed();
            };
        } finally {

            if (isInitiator) {
                // Ends of the execution of the owner: entity manager must be cleaned
                this.entityManagerProvider.destroyLatestEntityManager();
            }
        }
    }

    /**
     * Proceeds method in a transaction.
     *
     * @param methodInvocation        The method invocation to proceed
     * @param transaction             The current entity transaction
     * @param transactionalAnnotation The current transaction annotation
     * @return Method invocation returned result
     * @throws Throwable If something goes wrong
     * @since 1.7.0
     */
    private Object proceedInTransaction(final MethodInvocation methodInvocation,
                                        final EntityTransaction transaction,
                                        final Transactional transactionalAnnotation) throws Throwable {

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
        }
    }
}
