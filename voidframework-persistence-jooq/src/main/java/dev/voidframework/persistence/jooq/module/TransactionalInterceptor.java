package dev.voidframework.persistence.jooq.module;

import com.google.inject.Inject;
import dev.voidframework.core.lang.Either;
import dev.voidframework.core.utils.ProxyDetectorUtils;
import dev.voidframework.persistence.AbstractTransactionalInterceptor;
import jakarta.persistence.TransactionRequiredException;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import org.aopalliance.intercept.MethodInvocation;
import org.jooq.DSLContext;

import java.util.Objects;
import java.util.Optional;

/**
 * Intercepts method call when annotation {@link Transactional} is used.
 */
public class TransactionalInterceptor extends AbstractTransactionalInterceptor {

    private DSLContextProvider dslContextProvider;

    /**
     * Sets the entity manager provider.
     *
     * @param dslContextProvider The DSL context provider
     */
    @Inject
    public void setDataSourceManagerProvider(final DSLContextProvider dslContextProvider) {

        this.dslContextProvider = dslContextProvider;
    }

    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {

        // Retrieves the transaction configuration
        final Transactional transactionalAnnotation = Optional.ofNullable(methodInvocation.getMethod().getAnnotation(Transactional.class))
            .orElseGet(() -> methodInvocation.getThis().getClass().getAnnotation(Transactional.class));

        // Create a new EntityManager for this current thread (must be done once)
        if (this.dslContextProvider.isDSLContextMustBeInitialized() || transactionalAnnotation.value() == Transactional.TxType.REQUIRES_NEW) {
            this.dslContextProvider.initializeNewDSLContext();
        }

        // Retrieve transaction
        final DSLContext dslContext = this.dslContextProvider.get();

        // Checks transaction context
        final boolean isTransactionActive = this.isTransactionActive(this.dslContextProvider.get());

        if (transactionalAnnotation.value() == Transactional.TxType.NOT_SUPPORTED) {
            // The configuration indicates that current method must run outside a transaction context
            if (isTransactionActive) {
                this.dslContextProvider.initializeNewDSLContext();
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
     */
    private boolean isTransactionActive(final DSLContext dslContext) {

        return dslContext.data()
            .keySet()
            .stream()
            .map(Object::toString)
            .anyMatch(toStringValue -> Objects.equals(toStringValue, "DATA_DEFAULT_TRANSACTION_PROVIDER_SAVEPOINTS"));
    }
}
