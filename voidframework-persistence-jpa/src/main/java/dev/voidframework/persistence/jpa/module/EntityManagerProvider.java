package dev.voidframework.persistence.jpa.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.zaxxer.hikari.hibernate.HikariConnectionProvider;
import dev.voidframework.datasource.DataSourceManager;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Entity manager provider. This provider is special, it exposes methods to manually manage
 * the {@link EntityManager} to return when a user calls the method {@link #get()}. These
 * methods are used to handle transaction with the {@link javax.transaction.Transactional}
 * annotation interceptor.
 */
@Singleton
public class EntityManagerProvider implements Provider<EntityManager> {

    private final String dataSourceName;
    private final ThreadLocal<Deque<EntityManager>> currentEntityManager;
    private Provider<DataSourceManager> dataSourceManagerProvider;
    private EntityManagerFactory entityManagerFactory;

    /**
     * Build a new instance.
     *
     * @param dataSourceName The data source name
     */
    public EntityManagerProvider(final String dataSourceName) {
        this.dataSourceName = dataSourceName;
        this.currentEntityManager = new ThreadLocal<>();
        this.currentEntityManager.set(new ArrayDeque<>());
    }

    /**
     * Sets the data source manager provider.
     *
     * @param dataSourceManagerProvider The data source manager provider
     */
    @Inject
    public void setDataSourceManagerProvider(final Provider<DataSourceManager> dataSourceManagerProvider) {
        this.dataSourceManagerProvider = dataSourceManagerProvider;
    }

    @Override
    public EntityManager get() {
        if (this.hasAtLeastOneEntityManagerInitialized()) {
            createEntityManagerFactoryIfNeeded();
            return this.entityManagerFactory.createEntityManager();
        }

        return this.currentEntityManager.get().getFirst();
    }

    /**
     * Initializes a new entity manager for the current Thread.
     */
    public void initializeNewEntityFactoryManager() {
        createEntityManagerFactoryIfNeeded();
        this.currentEntityManager.get().addFirst(this.entityManagerFactory.createEntityManager());
    }

    /**
     * Destroys the latest entity manager initialized for the current Thread.
     */
    public void destroyLatestEntityManager() {
        final EntityManager entityManager = this.currentEntityManager.get().removeFirst();

        if (entityManager != null) {
            entityManager.close();
        }
    }

    /**
     * Checks if, at least, one entity manager is initialized for the current Thread.
     *
     * @return {@code true} at least one entity manager is initialized, otherwise, {@code false}
     */
    public boolean hasAtLeastOneEntityManagerInitialized() {
        return this.currentEntityManager.get().isEmpty();
    }

    /**
     * Creates the entity manager factory, if needed.
     */
    private void createEntityManagerFactoryIfNeeded() {
        if (this.entityManagerFactory == null) {
            this.entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(
                new PersistenceUnitInfoIml(dataSourceName),
                Map.of(AvailableSettings.DATASOURCE, this.dataSourceManagerProvider.get().getDataSource(dataSourceName)));
        }
    }

    /**
     * Persistence unit information.
     */
    private static class PersistenceUnitInfoIml implements PersistenceUnitInfo {

        private final String persistenceUnitName;

        /**
         * Build a new instance.
         *
         * @param persistenceUnitName Name of the persistence unit
         */
        private PersistenceUnitInfoIml(final String persistenceUnitName) {
            super();
            this.persistenceUnitName = persistenceUnitName;
        }

        @Override
        public String getPersistenceUnitName() {
            return this.persistenceUnitName;
        }

        @Override
        public String getPersistenceProviderClassName() {
            return HikariConnectionProvider.class.getName();
        }

        @Override
        public PersistenceUnitTransactionType getTransactionType() {
            return PersistenceUnitTransactionType.RESOURCE_LOCAL;
        }

        @Override
        public DataSource getJtaDataSource() {
            return null;
        }

        @Override
        public DataSource getNonJtaDataSource() {
            return null;
        }

        @Override
        public List<String> getMappingFileNames() {
            return Collections.emptyList();
        }

        @Override
        public List<URL> getJarFileUrls() {
            try {
                return Collections.list(this.getClass().getClassLoader().getResources(""));
            } catch (final IOException ignore) {
                return Collections.emptyList();
            }
        }

        @Override
        public URL getPersistenceUnitRootUrl() {
            return null;
        }

        @Override
        public List<String> getManagedClassNames() {
            return Collections.emptyList();
        }

        @Override
        public boolean excludeUnlistedClasses() {
            return false;
        }

        @Override
        public SharedCacheMode getSharedCacheMode() {
            return null;
        }

        @Override
        public ValidationMode getValidationMode() {
            return null;
        }

        @Override
        public Properties getProperties() {
            return new Properties();
        }

        @Override
        public String getPersistenceXMLSchemaVersion() {
            return null;
        }

        @Override
        public ClassLoader getClassLoader() {
            return null;
        }

        @Override
        public void addTransformer(final ClassTransformer classTransformer) {
        }

        @Override
        public ClassLoader getNewTempClassLoader() {
            return null;
        }
    }
}
