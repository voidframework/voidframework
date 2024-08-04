package dev.voidframework.bucket4j;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.bucket4j.exception.BucketTokenException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.local.LocalBucket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.Duration;
import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class BucketTokenRegistryTest {

    @Test
    void bucket() {

        // Arrange
        final Map<String, Bucket> bucketPerNameMap = Map.of(
            "bucketAPI1",
            Bucket.builder().addLimit(Bandwidth.simple(12, Duration.ofHours(1)).withId("one")).build(),
            "bucketAPI2",
            Bucket.builder().addLimit(Bandwidth.simple(100, Duration.ofDays(1)).withId("two")).build());
        final BucketTokenRegistry bucketTokenRegistry = new BucketTokenRegistry(null, bucketPerNameMap);

        // Act
        final Bucket value = bucketTokenRegistry.bucket("bucketAPI1");

        // Assert
        final LocalBucket localBucket1 = (LocalBucket) value;
        Assertions.assertNotNull(localBucket1);
        Assertions.assertEquals(1, localBucket1.getConfiguration().getBandwidths().length);
        Assertions.assertEquals("one", localBucket1.getConfiguration().getBandwidths()[0].getId());
    }

    @Test
    void bucketFromConfig() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("""
            voidframework.bucket4j.bucketAPI1.synchronizationStrategy = "LOCK_FREE"

            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.id = "one"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.capacity = 60
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.strategy = "GREEDY"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.tokens = 60
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.period = "1 minutes"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.initialTokens = 24
            """);
        final BucketTokenRegistry bucketTokenRegistry = new BucketTokenRegistry(configuration);

        // Act
        final Bucket value = bucketTokenRegistry.bucket("bucketAPI1");

        // Assert
        final LocalBucket localBucket1 = (LocalBucket) value;
        Assertions.assertNotNull(localBucket1);
        Assertions.assertEquals(1, localBucket1.getConfiguration().getBandwidths().length);
        Assertions.assertEquals("one", localBucket1.getConfiguration().getBandwidths()[0].getId());
        Assertions.assertEquals(60, localBucket1.getConfiguration().getBandwidths()[0].getCapacity());
        Assertions.assertEquals(24, localBucket1.getConfiguration().getBandwidths()[0].getInitialTokens());
        Assertions.assertEquals(60, localBucket1.getConfiguration().getBandwidths()[0].getRefillTokens());
        Assertions.assertEquals(60000000000L, localBucket1.getConfiguration().getBandwidths()[0].getRefillPeriodNanos());
        Assertions.assertFalse(localBucket1.getConfiguration().getBandwidths()[0].isRefillIntervally());
    }

    @Test
    void bucketConfigurationPathIsNull() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("");
        final BucketTokenRegistry bucketTokenRegistry = new BucketTokenRegistry(configuration);

        // Act
        final Exception exception = Assertions.assertThrows(
            BucketTokenException.BucketDoesNotExist.class,
            () -> bucketTokenRegistry.bucket("bucketAPI1", null));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("Bucket 'bucketAPI1' does not exist", exception.getMessage());
    }

    @Test
    void bucketOrDie_die() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("");
        final BucketTokenRegistry bucketTokenRegistry = new BucketTokenRegistry(configuration);

        // Act
        final Exception exception = Assertions.assertThrows(
            BucketTokenException.BucketDoesNotExist.class,
            () -> bucketTokenRegistry.bucketOrDie("bucketAPI1"));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("Bucket 'bucketAPI1' does not exist", exception.getMessage());
    }

    @Test
    void bucketOrDie_success() {

        // Arrange
        final Config configuration = ConfigFactory.parseString("""
            voidframework.bucket4j.bucketAPI1.synchronizationStrategy = "LOCK_FREE"

            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.id = "one"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.capacity = 60
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.strategy = "GREEDY"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.tokens = 60
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.period = "1 minutes"
            voidframework.bucket4j.bucketAPI1.bandwidthLimits.0.refill.initialTokens = 24
            """);
        final BucketTokenRegistry bucketTokenRegistry = new BucketTokenRegistry(configuration);

        // Create the bucket
        bucketTokenRegistry.bucket("bucketAPI1");

        // Act
        final Bucket bucket = bucketTokenRegistry.bucketOrDie("bucketAPI1");

        // Assert
        final LocalBucket localBucket1 = (LocalBucket) bucket;
        Assertions.assertNotNull(localBucket1);
        Assertions.assertEquals(1, localBucket1.getConfiguration().getBandwidths().length);
        Assertions.assertEquals("one", localBucket1.getConfiguration().getBandwidths()[0].getId());
        Assertions.assertEquals(60, localBucket1.getConfiguration().getBandwidths()[0].getCapacity());
        Assertions.assertEquals(24, localBucket1.getConfiguration().getBandwidths()[0].getInitialTokens());
        Assertions.assertEquals(60, localBucket1.getConfiguration().getBandwidths()[0].getRefillTokens());
        Assertions.assertEquals(60000000000L, localBucket1.getConfiguration().getBandwidths()[0].getRefillPeriodNanos());
        Assertions.assertFalse(localBucket1.getConfiguration().getBandwidths()[0].isRefillIntervally());
    }

    @Test
    void getAllBuckets() {

        // Arrange
        final Map<String, Bucket> bucketPerNameMap = Map.of(
            "bucketAPI1",
            Bucket.builder().addLimit(Bandwidth.simple(12, Duration.ofHours(1)).withId("one")).build(),
            "bucketAPI2",
            Bucket.builder().addLimit(Bandwidth.simple(100, Duration.ofDays(1)).withId("two")).build());
        final BucketTokenRegistry bucketTokenRegistry = new BucketTokenRegistry(null, bucketPerNameMap);

        // Act
        final Map<String, Bucket> value = bucketTokenRegistry.getAllBuckets();

        // Assert
        Assertions.assertNotNull(value);
        Assertions.assertEquals(2, value.size());

        final LocalBucket localBucket1 = (LocalBucket) value.get("bucketAPI1");
        Assertions.assertNotNull(localBucket1);
        Assertions.assertEquals(1, localBucket1.getConfiguration().getBandwidths().length);
        Assertions.assertEquals("one", localBucket1.getConfiguration().getBandwidths()[0].getId());

        final LocalBucket localBucket2 = (LocalBucket) value.get("bucketAPI2");
        Assertions.assertNotNull(localBucket2);
        Assertions.assertEquals(1, localBucket2.getConfiguration().getBandwidths().length);
        Assertions.assertEquals("two", localBucket2.getConfiguration().getBandwidths()[0].getId());
    }
}
