package dev.voidframework.persistence.jooq.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import dev.voidframework.core.utils.ConfigurationUtils;
import dev.voidframework.datasource.exception.DataSourceException;
import jakarta.transaction.Transactional;
import org.aopalliance.intercept.MethodInterceptor;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.tools.jdbc.JDBCUtils;

import java.util.Set;

/**
 * jOOQ module.
 *
 * @since 1.4.0
 */
public final class JooqModule extends AbstractModule {

    private final Config configuration;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @since 1.4.0
     */
    public JooqModule(final Config configuration) {

        this.configuration = configuration;
    }

    @Override
    protected void configure() {

        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");

        final Set<String> dataSourceNameSet = ConfigurationUtils.getAllRootLevelPaths(this.configuration, "voidframework.datasource");
        if (dataSourceNameSet.isEmpty()) {
            throw new DataSourceException.NotConfigured();
        }

        for (final String dataSourceName : dataSourceNameSet) {
            final SQLDialect sqlDialect = this.identifySQLDialect(dataSourceName);
            final DSLContextProvider dslContextProvider = new DSLContextProvider(dataSourceName, sqlDialect);
            requestInjection(dslContextProvider);

            bind(DSLContext.class).annotatedWith(Names.named(dataSourceName)).toProvider(dslContextProvider);
            bind(DSLContextProvider.class).annotatedWith(Names.named(dataSourceName)).toInstance(dslContextProvider);

            if (dataSourceName.equals("default")) {
                bind(DSLContext.class).toProvider(dslContextProvider);
                bind(DSLContextProvider.class).toInstance(dslContextProvider);
            }
        }

        final MethodInterceptor methodInterceptor = new TransactionalInterceptor();
        requestInjection(methodInterceptor);
        bindInterceptor(Matchers.annotatedWith(Transactional.class), Matchers.any(), methodInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), methodInterceptor);
    }

    /**
     * Identifies the SQL dialect.
     *
     * @param dataSourceName The data source name
     * @return Identified SQL dialect
     * @since 1.4.0
     */
    private SQLDialect identifySQLDialect(final String dataSourceName) {

        final String dataSourceUrl = this.configuration.getString("voidframework.datasource." + dataSourceName + ".driver");
        return JDBCUtils.dialect(dataSourceUrl);
    }
}
