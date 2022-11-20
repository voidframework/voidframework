package dev.voidframework.persistence.jooq.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import dev.voidframework.datasource.exception.DataSourceException;
import dev.voidframework.datasource.utils.DataSourceUtils;
import jakarta.transaction.Transactional;
import org.aopalliance.intercept.MethodInterceptor;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;

import java.util.Set;

/**
 * jOOQ module.
 */
public class JooqModule extends AbstractModule {

    private final Config configuration;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    public JooqModule(final Config configuration) {

        this.configuration = configuration;
    }

    @Override
    protected void configure() {

        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");

        final Set<String> dbConfigurationNameSet = DataSourceUtils.getAllDataSourceNames(this.configuration);
        if (dbConfigurationNameSet.isEmpty()) {
            throw new DataSourceException.NotConfigured();
        }

        for (final String dbConfigurationName : dbConfigurationNameSet) {
            final DSLContextProvider dslContextProvider = new DSLContextProvider(dbConfigurationName, SQLDialect.H2);
            requestInjection(dslContextProvider);

            bind(DSLContext.class).annotatedWith(Names.named(dbConfigurationName)).toProvider(dslContextProvider);
            bind(DSLContextProvider.class).annotatedWith(Names.named(dbConfigurationName)).toInstance(dslContextProvider);

            if (dbConfigurationName.equals("default")) {
                bind(DSLContext.class).toProvider(dslContextProvider);
                bind(DSLContextProvider.class).toInstance(dslContextProvider);
            }
        }

        final MethodInterceptor methodInterceptor = new TransactionalInterceptor();
        requestInjection(methodInterceptor);
        bindInterceptor(Matchers.annotatedWith(Transactional.class), Matchers.any(), methodInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), methodInterceptor);
    }
}
