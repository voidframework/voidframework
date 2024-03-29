package dev.voidframework.persistence.hibernate.module;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import dev.voidframework.core.bindable.Bindable;
import dev.voidframework.core.lifecycle.LifeCycleStart;
import dev.voidframework.core.utils.ConfigurationUtils;
import dev.voidframework.datasource.exception.DataSourceException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.Set;

/**
 * This LifeCycle allows to force the initialization of the EntityManagerFactory.
 *
 * @since 1.2.0
 */
@Bindable
public final class PersistenceLifeCycle {

    private final Config configuration;
    private final Injector injector;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @param injector      The injector instance
     * @since 1.2.0
     */
    @Inject
    public PersistenceLifeCycle(final Config configuration,
                                final Injector injector) {

        this.configuration = configuration;
        this.injector = injector;
    }

    /**
     * Force {@link EntityManagerFactory} initialization for each configured data sources.
     *
     * @since 1.2.0
     */
    @LifeCycleStart(priority = 51)
    public void forceEntityManagerFactoryInitialisation() {

        final Set<String> dataSourceNameSet = ConfigurationUtils.getAllRootLevelPaths(this.configuration, "voidframework.datasource");
        if (dataSourceNameSet.isEmpty()) {
            throw new DataSourceException.NotConfigured();
        }

        for (final String dataSourceName : dataSourceNameSet) {
            final Key<EntityManager> key = Key.get(EntityManager.class, Names.named(dataSourceName));
            final Provider<EntityManager> entityManagerProvider = this.injector.getProvider(key);

            final EntityManager entityManager = entityManagerProvider.get();
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }
}
