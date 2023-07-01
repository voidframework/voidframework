package dev.voidframework.bucket4j.module;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.matcher.Matchers;
import com.typesafe.config.Config;
import dev.voidframework.bucket4j.BucketTokenRegistry;
import dev.voidframework.bucket4j.annotation.BucketToken;
import dev.voidframework.core.module.OrderedModule;
import dev.voidframework.core.utils.ConfigurationUtils;

import java.util.Set;

/**
 * Bucket4J module.
 *
 * @since 1.9.0
 */
public final class Bucket4JModule extends AbstractModule implements OrderedModule {

    private final Config configuration;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @since 1.9.0
     */
    @Inject
    public Bucket4JModule(final Config configuration) {

        this.configuration = configuration;
    }

    @Override
    protected void configure() {

        // Create Bucket registry
        final BucketTokenRegistry bucketTokenRegistry = new BucketTokenRegistry(this.configuration);
        bind(BucketTokenRegistry.class).toInstance(bucketTokenRegistry);

        // Force creation of all Buckets
        final Set<String> bucketNameSet = ConfigurationUtils.getAllRootLevelPaths(this.configuration, "voidframework.bucket4j");
        for (final String bucketName : bucketNameSet) {
            bucketTokenRegistry.bucket(bucketName);
        }

        // Create method call interceptor
        final BucketTokenInterceptor bucketTokenInterceptor = new BucketTokenInterceptor(bucketTokenRegistry);

        requestInjection(bucketTokenInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(BucketToken.class), bucketTokenInterceptor);
        bindInterceptor(Matchers.annotatedWith(BucketToken.class), Matchers.any(), bucketTokenInterceptor);
    }

    @Override
    public int priority() {

        return 500;
    }
}
