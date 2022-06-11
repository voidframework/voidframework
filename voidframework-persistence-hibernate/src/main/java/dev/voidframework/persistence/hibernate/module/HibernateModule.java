package dev.voidframework.persistence.hibernate.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Hibernate module.
 */
public class HibernateModule extends AbstractModule {

    private final Config configuration;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    public HibernateModule(final Config configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        if (!this.configuration.hasPathOrNull("voidframework.datasource")) {
            throw new RuntimeException("DataSource is not configured");
        }

        final Set<String> dbConfigurationNameSet = this.configuration.getConfig("voidframework.datasource").entrySet()
            .stream()
            .map(Map.Entry::getKey)
            .map(key -> {
                if (key.contains(".")) {
                    return key.substring(0, key.indexOf("."));
                } else {
                    return key;
                }
            })
            .collect(Collectors.toSet());

        if (dbConfigurationNameSet.isEmpty()) {
            throw new RuntimeException("DataSource is not configured");
        }

        for (final String dbConfigurationName : dbConfigurationNameSet) {
            final EntityManagerProvider entityManagerProvider = new EntityManagerProvider(dbConfigurationName);
            requestInjection(entityManagerProvider);
            bind(EntityManager.class).annotatedWith(Names.named(dbConfigurationName)).toProvider(entityManagerProvider);

            if (dbConfigurationName.equals("default")) {
                bind(EntityManager.class).toProvider(entityManagerProvider);
                bind(EntityManagerProvider.class).toInstance(entityManagerProvider);
            }
        }

        final MethodInterceptor methodInterceptor = new TransactionalInterceptor();
        requestInjection(methodInterceptor);
        bindInterceptor(Matchers.annotatedWith(Transactional.class), Matchers.any(), methodInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), methodInterceptor);
    }
}
