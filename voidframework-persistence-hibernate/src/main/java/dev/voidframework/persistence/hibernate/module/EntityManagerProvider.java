package dev.voidframework.persistence.hibernate.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.datasource.DataSourceManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Entity manager provider. This provider is special, it exposes methods to manually manage
 * the {@link EntityManager} to return when a user calls the method {@link #get()}. These
 * methods are used to handle transaction with the {@link jakarta.transaction.Transactional}
 * annotation interceptor.
 *
 * @since 1.0.0
 */
@Singleton
public class EntityManagerProvider implements Provider<EntityManager> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManagerProvider.class);

    private final String dataSourceName;
    private final String modelsJarUrlPattern;
    private final ThreadLocal<Deque<EntityManager>> currentEntityManager;
    private Provider<DataSourceManager> dataSourceManagerProvider;
    private EntityManagerFactory entityManagerFactory;

    /**
     * Build a new instance.
     *
     * @param dataSourceName      The data source name
     * @param modelsJarUrlPattern The pattern to identify JAR containing models
     * @since 1.0.0
     */
    public EntityManagerProvider(final String dataSourceName, final String modelsJarUrlPattern) {

        this.dataSourceName = dataSourceName;
        this.modelsJarUrlPattern = modelsJarUrlPattern;
        this.currentEntityManager = new ThreadLocal<>();
    }

    /**
     * Sets the data source manager provider.
     *
     * @param dataSourceManagerProvider The data source manager provider
     * @since 1.0.0
     */
    @Inject
    public void setDataSourceManagerProvider(final Provider<DataSourceManager> dataSourceManagerProvider) {

        this.dataSourceManagerProvider = dataSourceManagerProvider;
    }

    @Override
    public EntityManager get() {

        if (this.isEntityManagerMustBeInitialized()) {
            createEntityManagerFactoryIfNeeded();
            return this.entityManagerFactory.createEntityManager();
        }

        return this.currentEntityManager.get().getFirst();
    }

    /**
     * Initializes a new entity manager for the current Thread.
     *
     * @since 1.0.0
     */
    public void initializeNewEntityFactoryManager() {

        createEntityManagerFactoryIfNeeded();

        Deque<EntityManager> currentEntityManagerDeque = this.currentEntityManager.get();
        if (currentEntityManagerDeque == null) {
            currentEntityManagerDeque = new ArrayDeque<>();
            this.currentEntityManager.set(currentEntityManagerDeque);
        }
        currentEntityManagerDeque.addFirst(this.entityManagerFactory.createEntityManager());
    }

    /**
     * Destroys the latest entity manager initialized for the current Thread.
     *
     * @since 1.0.0
     */
    public void destroyLatestEntityManager() {

        final EntityManager entityManager = this.currentEntityManager.get().removeFirst();

        if (entityManager != null) {
            entityManager.close();
        }

        if (this.currentEntityManager.get().isEmpty()) {
            this.currentEntityManager.remove();
        }
    }

    /**
     * Checks if, at least, one entity manager is initialized for the current Thread.
     *
     * @return {@code true} at least one entity manager is initialized, otherwise, {@code false}
     * @since 1.0.0
     */
    public boolean isEntityManagerMustBeInitialized() {

        final Deque<EntityManager> currentEntityManagerDeque = this.currentEntityManager.get();
        return currentEntityManagerDeque == null || currentEntityManagerDeque.isEmpty();
    }

    /**
     * Creates the entity manager factory, if needed.
     *
     * @since 1.0.0
     */
    private void createEntityManagerFactoryIfNeeded() {

        if (this.entityManagerFactory == null) {
            // Creates a list containing all JARs to use to find "Model" classes
            final List<URL> javaFileUrlList = createModelsJarFileUrls();

            // Creates entity manager
            this.entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(
                new PersistenceUnitInfoIml(dataSourceName, javaFileUrlList),
                Map.of(
                    AvailableSettings.DATASOURCE, this.dataSourceManagerProvider.get().getDataSource(dataSourceName),
                    AvailableSettings.HBM2DDL_AUTO, "none"));
        }
    }

    /**
     * Creates a list of JAR files who can contain models.
     *
     * @return A list of JAR file URLs
     * @since 1.1.2
     */
    private List<URL> createModelsJarFileUrls() {

        try {
            final List<String> javaFilePathList = new ArrayList<>();
            String urlAsString;
            int idx;

            if (this.modelsJarUrlPattern != null) {
                final List<URL> urlList = Collections.list(this.getClass().getClassLoader().getResources(StringConstants.EMPTY));
                urlList.addAll(Collections.list(this.getClass().getClassLoader().getResources("META-INF")));

                for (final URL url : urlList) {
                    urlAsString = url.toString();
                    idx = urlAsString.indexOf("/META-INF") + 1;
                    if (idx > 0) {
                        urlAsString = urlAsString.substring(0, idx);
                    }

                    if (urlAsString.matches(this.modelsJarUrlPattern)) {
                        javaFilePathList.add(urlAsString);
                    }
                }
            }

            final URL url = this.getClass().getResource("/application.conf");
            if (url != null) {
                urlAsString = url.toString();
                idx = urlAsString.indexOf("/application.conf") + 1;
                if (idx > 0) {
                    urlAsString = urlAsString.substring(0, idx);
                }

                javaFilePathList.add(0, urlAsString);
            }

            return javaFilePathList.stream()
                .distinct()
                .map(this::createURL)
                .filter(Objects::nonNull)
                .toList();
        } catch (final IOException ex) {
            LOGGER.error("Can't create list of models Jar file URLs", ex);
        }

        return Collections.emptyList();
    }

    /**
     * Create a URL from a String.
     *
     * @param urlAsString The URL as String
     * @return The newly created URL
     * @since 1.1.2
     */
    private URL createURL(final String urlAsString) {

        try {
            return new URL(urlAsString);
        } catch (final MalformedURLException ex) {
            LOGGER.error("Can't create URL", ex);
        }

        return null;
    }

    /**
     * Persistence unit information.
     *
     * @since 1.0.0
     */
    private static class PersistenceUnitInfoIml implements PersistenceUnitInfo {

        private final String persistenceUnitName;
        private final List<URL> jarUrlList;

        /**
         * Build a new instance.
         *
         * @param persistenceUnitName Name of the persistence unit
         * @param jarUrlList          JAR URLs
         * @since 1.0.0
         */
        private PersistenceUnitInfoIml(final String persistenceUnitName,
                                       final List<URL> jarUrlList) {

            super();
            this.persistenceUnitName = persistenceUnitName;
            this.jarUrlList = jarUrlList;
        }

        @Override
        public String getPersistenceUnitName() {

            return this.persistenceUnitName;
        }

        @Override
        public String getPersistenceProviderClassName() {

            return null;
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

            return this.jarUrlList;
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

            // Nothing to do
        }

        @Override
        public ClassLoader getNewTempClassLoader() {

            return null;
        }
    }
}
