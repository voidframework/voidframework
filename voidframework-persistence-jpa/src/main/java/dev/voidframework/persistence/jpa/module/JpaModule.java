package dev.voidframework.persistence.jpa.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import org.aopalliance.intercept.MethodInterceptor;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JPA module.
 */
public class JpaModule extends AbstractModule {

    private final Config configuration;

    /**
     * Build a new instance.
     *
     * @param configuration The current configuration
     */
    public JpaModule(final Config configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
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

        for (final String dbConfigurationName : dbConfigurationNameSet) {
            final dev.voidframework.persistence.jpa.module.EntityManagerProvider entityManagerProvider = new dev.voidframework.persistence.jpa.module.EntityManagerProvider(dbConfigurationName);
            requestInjection(entityManagerProvider);
            bind(EntityManager.class).annotatedWith(Names.named(dbConfigurationName)).toProvider(entityManagerProvider);

            if( dbConfigurationName.equals("default")) {
                bind(EntityManager.class).toProvider(entityManagerProvider);
                bind(dev.voidframework.persistence.jpa.module.EntityManagerProvider.class).toInstance(entityManagerProvider);
            }
        }

        final MethodInterceptor methodInterceptor = new dev.voidframework.persistence.jpa.module.TransactionalInterceptor();
        requestInjection(methodInterceptor);
        bindInterceptor(Matchers.annotatedWith(Transactional.class), Matchers.any(), methodInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), methodInterceptor);
    }
}
