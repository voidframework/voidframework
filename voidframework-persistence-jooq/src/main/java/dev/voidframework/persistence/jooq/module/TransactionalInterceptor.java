package dev.voidframework.persistence.jooq.module;

import com.google.inject.Inject;
import dev.voidframework.core.lang.Either;
import dev.voidframework.core.utils.ProxyDetectorUtils;
import dev.voidframework.persistence.AbstractTransactionalInterceptor;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.TransactionRequiredException;
import jakarta.transaction.Transactional;
import org.aopalliance.intercept.MethodInvocation;
import org.jooq.DSLContext;

import java.util.Objects;

/**
 * Intercepts method call when annotation {@link Transactional} is used.
 *
 * @since 1.4.0
 */
public class TransactionalInterceptor extends AbstractTransactionalInterceptor {

    private DSLContextProvider dslContextProvider;

    /**
     * Sets the entity manager provider.
     *
     * @param dslContextProvider The DSL context provider
     * @since 1.4.0
     */
    @Inject
    public void setDataSourceManagerProvider(final DSLContextProvider dslContextProvider) {

        this.dslContextProvider = dslContextProvider;
    }

    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {

        boolean isInitiator = false;

        // Retrieves the transaction configuration
        Transactional transactionalAnnotation = methodInvocation.getMethod().getAnnotation(Transactional.class);
        if (transactionalAnnotation == null) {
            transactionalAnnotation = methodInvocation.getThis().getClass().getAnnotation(Transactional.class);
        }

        // Create a new EntityManager for this current thread (must be done once)
        if (this.dslContextProvider.isDSLContextMustBeInitialized()) {
            this.dslContextProvider.initializeNewDSLContext();
            isInitiator = true;
        }

        // Retrieve transaction
        final DSLContext dslContext = this.dslContextProvider.get();

        try {
            return switch (transactionalAnnotation.value()) {
                case MANDATORY:
                    if (!this.isTransactionActive(dslContext)) {
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
                    if (this.isTransactionActive(dslContext)) {
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
                    if (this.isTransactionActive(dslContext)) {
                        isInitiator = true;
                        this.dslContextProvider.initializeNewDSLContext();
                    }
                    yield methodInvocation.proceed();
                case REQUIRED:
                    if (!this.isTransactionActive(dslContext)) {
                        yield this.proceedInTransaction(methodInvocation, dslContext, transactionalAnnotation);
                    }
                    yield methodInvocation.proceed();
                case REQUIRES_NEW:
                    // A new transaction must be created in any case
                    if (!isInitiator) {
                        isInitiator = true;

                        this.dslContextProvider.initializeNewDSLContext();
                        final DSLContext newDSLContext = this.dslContextProvider.get();

                        yield this.proceedInTransaction(methodInvocation, newDSLContext, transactionalAnnotation);
                    } else {
                        yield this.proceedInTransaction(methodInvocation, dslContext, transactionalAnnotation);
                    }
                case SUPPORTS:
                    // A transaction exist or the configuration indicates than method
                    // can be call with or without an existing transaction
                    yield methodInvocation.proceed();
            };
        } finally {

            if (isInitiator) {
                // Ends of the execution of the owner: DSL context must be cleaned
                this.dslContextProvider.destroyLatestDSLContext();
            }
        }
    }

    /**
     * Proceeds method in a transaction.
     *
     * @param methodInvocation        The method invocation to proceed
     * @param dslContext              The current DSL context
     * @param transactionalAnnotation The current transaction annotation
     * @return Method invocation returned result
     * @throws Throwable If something goes wrong
     * @since 1.7.0
     */
    private Object proceedInTransaction(final MethodInvocation methodInvocation,
                                        final DSLContext dslContext,
                                        final Transactional transactionalAnnotation) throws Throwable {

        // Creates a new transaction and then executes the method. If something goes
        // wrong, and depending on the configuration, a rollback will be performed
        final Either<Object, Throwable> result = dslContext.transactionResult((configuration) -> {
            try {
                this.dslContextProvider.replaceExistingDSLContext(configuration.dsl());
                return Either.ofLeft(methodInvocation.proceed());
            } catch (final Throwable throwable) {
                if (this.hasToRollback(transactionalAnnotation, throwable.getClass())) {
                    throw throwable;
                }

                return Either.ofRight(throwable);
            } finally {
                this.dslContextProvider.destroyLatestDSLContext();
            }
        });

        if (result.hasRight()) {
            throw result.getRight();
        }

        return result.getLeft();
    }

    /**
     * Checks if the transaction is active.
     *
     * @param dslContext The DSL Context
     * @return {@code true} if the transaction is active, otherwise, {@code false}
     * @since 1.4.0
     */
    private boolean isTransactionActive(final DSLContext dslContext) {

        return dslContext.data()
            .keySet()
            .stream()
            .map(Object::toString)
            .anyMatch(toStringValue -> Objects.equals(toStringValue, "DATA_DEFAULT_TRANSACTION_PROVIDER_SAVEPOINTS"));
    }
}
