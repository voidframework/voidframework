package dev.voidframework.persistence.hibernate.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import dev.voidframework.datasource.exception.DataSourceException;
import dev.voidframework.datasource.utils.DataSourceUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.Set;

/**
 * Hibernate module.
 *
 * @since 1.0.0
 */
public final class HibernateModule extends AbstractModule {

    private final Config configuration;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @since 1.0.0
     */
    public HibernateModule(final Config configuration) {

        this.configuration = configuration;
    }

    @Override
    protected void configure() {

        if (!this.configuration.hasPathOrNull("voidframework.datasource")) {
            throw new DataSourceException.NotConfigured();
        }

        final Set<String> dataSourceNameSet = DataSourceUtils.getAllDataSourceNames(this.configuration);
        if (dataSourceNameSet.isEmpty()) {
            throw new DataSourceException.NotConfigured();
        }

        String modelsJarUrlPattern = this.configuration.getString("voidframework.persistence.modelsJarUrlPattern");
        if (modelsJarUrlPattern != null && modelsJarUrlPattern.equalsIgnoreCase("auto")) {
            modelsJarUrlPattern = "(.*)";
        }

        for (final String dataSourceName : dataSourceNameSet) {
            final EntityManagerProvider entityManagerProvider = new EntityManagerProvider(dataSourceName, modelsJarUrlPattern);
            requestInjection(entityManagerProvider);
            bind(EntityManager.class).annotatedWith(Names.named(dataSourceName)).toProvider(entityManagerProvider);

            if (dataSourceName.equals("default")) {
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
