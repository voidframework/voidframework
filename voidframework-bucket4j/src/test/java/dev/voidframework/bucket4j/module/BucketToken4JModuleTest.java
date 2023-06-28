package dev.voidframework.bucket4j.module;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.bucket4j.BucketTokenRegistry;
import dev.voidframework.bucket4j.exception.BucketTokenException;
import io.github.bucket4j.local.LocalBucket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class BucketToken4JModuleTest {

    @Test
    void bucket4JModuleWithGreedyBucket() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("""
            voidframework.bucket4j.bucketAPI1.synchronizationStrategy = "LOCK_FREE"

            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.id = "business-limit"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.capacity = 60
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.strategy = "GREEDY"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.tokens = 60
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.period = "1 minutes"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.initialTokens = 24

            voidframework.bucket4j.bucketAPI1.bandwidthLimits.1.id = "burst-limit"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.1.capacity = 1000
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.1.refill.strategy = "GREEDY"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.1.refill.tokens = 1000
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.1.refill.period = "1 hours"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.1.refill.initialTokens = 24
            """);

        // Act
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {

            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                install(new Bucket4JModule(configuration));
            }
        });

        final BucketTokenRegistry bucketTokenRegistry = injector.getInstance(BucketTokenRegistry.class);

        // Assert
        final LocalBucket localBucket1 = (LocalBucket) bucketTokenRegistry.bucket("bucketAPI1");
        Assertions.assertNotNull(localBucket1);
        Assertions.assertEquals(2, localBucket1.getConfiguration().getBandwidths().length);

        Assertions.assertEquals("business-limit", localBucket1.getConfiguration().getBandwidths()[0].getId());
        Assertions.assertEquals(60, localBucket1.getConfiguration().getBandwidths()[0].getCapacity());
        Assertions.assertEquals(24, localBucket1.getConfiguration().getBandwidths()[0].getInitialTokens());
        Assertions.assertEquals(60, localBucket1.getConfiguration().getBandwidths()[0].getRefillTokens());
        Assertions.assertEquals(60000000000L, localBucket1.getConfiguration().getBandwidths()[0].getRefillPeriodNanos());
        Assertions.assertFalse(localBucket1.getConfiguration().getBandwidths()[0].isRefillIntervally());

        Assertions.assertEquals("burst-limit", localBucket1.getConfiguration().getBandwidths()[1].getId());
        Assertions.assertEquals(1000, localBucket1.getConfiguration().getBandwidths()[1].getCapacity());
        Assertions.assertEquals(24, localBucket1.getConfiguration().getBandwidths()[1].getInitialTokens());
        Assertions.assertEquals(1000, localBucket1.getConfiguration().getBandwidths()[1].getRefillTokens());
        Assertions.assertEquals(3600000000000L, localBucket1.getConfiguration().getBandwidths()[1].getRefillPeriodNanos());
        Assertions.assertFalse(localBucket1.getConfiguration().getBandwidths()[1].isRefillIntervally());
    }

    @Test
    void bucket4JModuleWithIntervallyBucket() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("""
            voidframework.bucket4j.bucketAPI1.synchronizationStrategy = "LOCK_FREE"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.id = "limit-bucket-intervally"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.capacity = 138
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.strategy = "INTERVALLY"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.tokens = 30
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.period = "2 hours"
            """);

        // Act
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {

            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                install(new Bucket4JModule(configuration));
            }
        });

        final BucketTokenRegistry bucketTokenRegistry = injector.getInstance(BucketTokenRegistry.class);

        // Assert
        final LocalBucket localBucket = (LocalBucket) bucketTokenRegistry.bucket("bucketAPI1");
        Assertions.assertNotNull(localBucket);
        Assertions.assertEquals(1, localBucket.getConfiguration().getBandwidths().length);

        Assertions.assertEquals("limit-bucket-intervally", localBucket.getConfiguration().getBandwidths()[0].getId());
        Assertions.assertEquals(138, localBucket.getConfiguration().getBandwidths()[0].getCapacity());
        Assertions.assertEquals(138, localBucket.getConfiguration().getBandwidths()[0].getInitialTokens());
        Assertions.assertEquals(30, localBucket.getConfiguration().getBandwidths()[0].getRefillTokens());
        Assertions.assertEquals(7200000000000L, localBucket.getConfiguration().getBandwidths()[0].getRefillPeriodNanos());
        Assertions.assertTrue(localBucket.getConfiguration().getBandwidths()[0].isRefillIntervally());
        Assertions.assertFalse(localBucket.getConfiguration().getBandwidths()[0].isUseAdaptiveInitialTokens());
        Assertions.assertFalse(localBucket.getConfiguration().getBandwidths()[0].isIntervallyAligned());
    }

    @Test
    void bucket4JModuleWithIntervallyAlignedBucket() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("""
            voidframework.bucket4j.bucketAPI1.synchronizationStrategy = "LOCK_FREE"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.id = "limit-bucket-1"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.capacity = 120
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.strategy = "INTERVALLY_ALIGNED"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.tokens = 1
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.period = "1 hours"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.timeOfFirstRefill = "5 minutes"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.useAdaptiveInitialTokens = true
            """);

        // Act
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {

            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                install(new Bucket4JModule(configuration));
            }
        });

        final BucketTokenRegistry bucketTokenRegistry = injector.getInstance(BucketTokenRegistry.class);

        // Assert
        final LocalBucket localBucket = (LocalBucket) bucketTokenRegistry.bucket("bucketAPI1");
        Assertions.assertNotNull(localBucket);
        Assertions.assertEquals(1, localBucket.getConfiguration().getBandwidths().length);

        Assertions.assertEquals("limit-bucket-1", localBucket.getConfiguration().getBandwidths()[0].getId());
        Assertions.assertEquals(120, localBucket.getConfiguration().getBandwidths()[0].getCapacity());
        Assertions.assertEquals(120, localBucket.getConfiguration().getBandwidths()[0].getInitialTokens());
        Assertions.assertEquals(1, localBucket.getConfiguration().getBandwidths()[0].getRefillTokens());
        Assertions.assertEquals(3600000000000L, localBucket.getConfiguration().getBandwidths()[0].getRefillPeriodNanos());
        Assertions.assertTrue(localBucket.getConfiguration().getBandwidths()[0].isRefillIntervally());
        Assertions.assertTrue(localBucket.getConfiguration().getBandwidths()[0].isUseAdaptiveInitialTokens());
        Assertions.assertTrue(localBucket.getConfiguration().getBandwidths()[0].isIntervallyAligned());
    }

    @Test
    void bucket4JModuleWithUnknowRefillType() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("""
            voidframework.bucket4j.bucketAPI1.synchronizationStrategy = "LOCK_FREE"

            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.id = "business-limit"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.capacity = 60
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.strategy = "UNKNOWN"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.tokens = 60
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.period = "1 minutes"
            """);

        // Act
        final AbstractModule module = new AbstractModule() {

            @Override
            protected void configure() {
                bind(Config.class).toInstance(configuration);
                install(new Bucket4JModule(configuration));
            }
        };

        final CreationException exception = Assertions.assertThrows(
            CreationException.class,
            () -> Guice.createInjector(Stage.PRODUCTION, module));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertInstanceOf(BucketTokenException.UnknownRefillStrategy.class, exception.getCause());
        Assertions.assertEquals(
            "Unknown refill strategy 'UNKNOWN'. Accepted values are: GREEDY, INTERVALLY, or INTERVALLY_ALIGNED",
            exception.getCause().getMessage());
    }
}
