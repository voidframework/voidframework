package dev.voidframework.persistence.jooq.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import dev.voidframework.datasource.DataSourceManager;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * DSL context provider. This provider is special, it exposes methods to manually manage
 * the {@link DSLContext} to return when a user calls the method {@link #get()}. These
 * methods are used to handle transaction with the {@link jakarta.transaction.Transactional}
 * annotation interceptor.
 *
 * @since 1.4.0
 */
@Singleton
public class DSLContextProvider implements Provider<DSLContext> {

    private final String dataSourceName;
    private final SQLDialect sqlDialect;
    private final ThreadLocal<Deque<DSLContext>> currentDSLContext;
    private Provider<DataSourceManager> dataSourceManagerProvider;

    /**
     * Build a new instance.
     *
     * @param dataSourceName The data source name
     * @param sqlDialect     The SQL dialect
     * @since 1.4.0
     */
    public DSLContextProvider(final String dataSourceName, final SQLDialect sqlDialect) {

        this.dataSourceName = dataSourceName;
        this.sqlDialect = sqlDialect;
        this.currentDSLContext = new ThreadLocal<>();
    }

    /**
     * Sets the data source manager provider.
     *
     * @param dataSourceManagerProvider The data source manager provider
     * @since 1.4.0
     */
    @Inject
    public void setDataSourceManagerProvider(final Provider<DataSourceManager> dataSourceManagerProvider) {

        this.dataSourceManagerProvider = dataSourceManagerProvider;
    }

    @Override
    public DSLContext get() {

        if (this.isDSLContextMustBeInitialized()) {
            return this.createDSLContext();
        }

        return this.currentDSLContext.get().getFirst();
    }

    /**
     * Destroys the latest DSL context initialized for the current Thread.
     *
     * @since 1.4.0
     */
    public void destroyLatestDSLContext() {

        final Deque<DSLContext> currentDSLContextDeque = this.currentDSLContext.get();
        if (currentDSLContextDeque != null) {
            currentDSLContextDeque.removeFirst();

            if (currentDSLContextDeque.isEmpty()) {
                this.currentDSLContext.remove();
            }
        }
    }

    /**
     * Initializes a new DSL context for the current Thread.
     *
     * @since 1.4.0
     */
    public void initializeNewDSLContext() {

        Deque<DSLContext> currentDSLContextDeque = this.currentDSLContext.get();
        if (currentDSLContextDeque == null) {
            currentDSLContextDeque = new ArrayDeque<>();
            this.currentDSLContext.set(currentDSLContextDeque);
        }
        currentDSLContextDeque.addFirst(this.createDSLContext());
    }

    /**
     * Replaces the current DSL context with another one.
     *
     * @since 1.4.0
     */
    public void replaceExistingDSLContext(final DSLContext dslContext) {

        this.destroyLatestDSLContext();

        Deque<DSLContext> currentDSLContextDeque = this.currentDSLContext.get();
        if (currentDSLContextDeque == null) {
            currentDSLContextDeque = new ArrayDeque<>();
            this.currentDSLContext.set(currentDSLContextDeque);
        }
        currentDSLContextDeque.addFirst(dslContext);
    }

    /**
     * Checks if, at least, one DSL context is initialized for the current Thread.
     *
     * @return {@code true} at least one DSL context is initialized, otherwise, {@code false}
     * @since 1.4.0
     */
    public boolean isDSLContextMustBeInitialized() {

        final Deque<DSLContext> currentDSLContextDeque = this.currentDSLContext.get();
        return currentDSLContextDeque == null || currentDSLContextDeque.isEmpty();
    }

    /**
     * Creates a new DSL context.
     *
     * @return Newly created DSL context
     * @since 1.4.0
     */
    private DSLContext createDSLContext() {

        return DSL.using(
            this.dataSourceManagerProvider.get().getDataSource(this.dataSourceName),
            this.sqlDialect);
    }
}
