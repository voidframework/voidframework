package dev.voidframework.persistence.hibernate.module;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import dev.voidframework.core.bindable.BindClass;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.lifecycle.LifeCycleStart;
import dev.voidframework.exception.DataSourceException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This LifeCycle allows to force the initialization of the EntityManagerFactory.
 */
@BindClass
public final class PersistenceLifeCycle {

    private final Config configuration;
    private final Injector injector;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @param injector      The injector instance
     */
    @Inject
    public PersistenceLifeCycle(final Config configuration,
                                final Injector injector) {

        this.configuration = configuration;
        this.injector = injector;
    }

    /**
     * Force {@link EntityManagerFactory} initialization for each configured data sources.
     */
    @LifeCycleStart(priority = 51)
    public void forceEntityManagerFactoryInitialisation() {

        if (!this.configuration.hasPathOrNull("voidframework.datasource")) {
            throw new DataSourceException.NotConfigured();
        }

        final Set<String> dbConfigurationNameSet = this.configuration.getConfig("voidframework.datasource").entrySet()
            .stream()
            .map(Map.Entry::getKey)
            .map(key -> {
                if (key.contains(StringConstants.DOT)) {
                    return key.substring(0, key.indexOf(StringConstants.DOT));
                } else {
                    return key;
                }
            })
            .collect(Collectors.toSet());

        for (final String dbConfigurationName : dbConfigurationNameSet) {
            final Key<EntityManager> key = Key.get(EntityManager.class, Names.named(dbConfigurationName));
            final Provider<EntityManager> entityManagerProvider = this.injector.getProvider(key);

            final EntityManager entityManager = entityManagerProvider.get();
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }
}
