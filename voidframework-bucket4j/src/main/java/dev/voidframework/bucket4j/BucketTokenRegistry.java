package dev.voidframework.bucket4j;

import com.typesafe.config.Config;
import dev.voidframework.bucket4j.exception.BucketTokenException;
import dev.voidframework.core.utils.ConfigurationUtils;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucketBuilder;
import io.github.bucket4j.local.SynchronizationStrategy;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * This is a factory to create Bucket instances which
 * stores all Bucket instances in a registry.
 *
 * @since 1.9.0
 */
public final class BucketTokenRegistry {

    private final Config configuration;
    private final Map<String, Bucket> bucketPerNameMap;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @since 1.9.0
     */
    public BucketTokenRegistry(final Config configuration) {

        this.configuration = configuration;
        this.bucketPerNameMap = new ConcurrentHashMap<>();
    }

    /**
     * Build a new instance.
     *
     * @param configuration  The application configuration
     * @param initialBuckets Initial Buckets
     * @since 1.9.0
     */
    public BucketTokenRegistry(final Config configuration, final Map<String, Bucket> initialBuckets) {

        this.configuration = configuration;
        this.bucketPerNameMap = new ConcurrentHashMap<>(initialBuckets);
    }

    /**
     * Returns a managed Bucket or throw an exception if not exist.
     *
     * @param bucketName The name of the Bucket
     * @return The Bucket
     * @see BucketTokenException.BucketDoesNotExist
     * @since 1.9.0
     */
    public Bucket bucketOrDie(final String bucketName) {

        return bucket(bucketName, null);
    }

    /**
     * Returns a managed Bucket or create a new one.
     * All setting keys to configure Bucket must exist into "voidframework.bucket4j.&lt;bucketName&gt;".
     *
     * @param bucketName The name of the Bucket
     * @return The Bucket
     * @since 1.9.0
     */
    public Bucket bucket(final String bucketName) {

        return bucket(bucketName, "voidframework.bucket4j." + bucketName);
    }

    /**
     * Returns a managed Bucket or create a new one from the given configuration path.
     * If Bucket does not exist and "{@code configurationPath}" parameter is {@code null},
     * the exception {@code BucketDoesNotExist} will be thrown.
     *
     * @param bucketName        The name of the Bucket
     * @param configurationPath The configuration path to use during Bucket creation process
     * @return The Bucket
     * @see BucketTokenException.BucketDoesNotExist
     * @since 1.9.0
     */
    public Bucket bucket(final String bucketName, final String configurationPath) {

        Bucket managedBucket = this.bucketPerNameMap.get(bucketName);
        if (managedBucket == null) {
            if (configurationPath == null) {
                throw new BucketTokenException.BucketDoesNotExist(bucketName);
            }

            managedBucket = this.createBucket(configurationPath);
            this.bucketPerNameMap.put(bucketName, managedBucket);
        }

        return managedBucket;
    }

    /**
     * Returns all managed Bucket instances.
     *
     * @return All managed Bucket instances.
     * @since 1.9.0
     */
    public Map<String, Bucket> getAllBuckets() {

        return Map.copyOf(this.bucketPerNameMap);
    }

    /**
     * Creates a new Bucket.
     *
     * @param configurationPath The configuration path to use during Bucket creation process
     * @return Newly created Bucket
     * @since 1.9.0
     */
    private Bucket createBucket(final String configurationPath) {

        final Config bucketConfiguration = this.configuration.getConfig(configurationPath);
        final LocalBucketBuilder bucketBuilder = Bucket.builder();

        final SynchronizationStrategy synchronizationStrategy = ConfigurationUtils.getEnumOrDefault(
            bucketConfiguration,
            "synchronizationStrategy",
            SynchronizationStrategy.class,
            SynchronizationStrategy.LOCK_FREE);

        final List<? extends Config> bandwidthLimitConfigurationList = bucketConfiguration.getConfigList("bandwidthLimits");
        for (final Config config : bandwidthLimitConfigurationList) {
            final Bandwidth bandwidth = this.createBandwidth(config);
            bucketBuilder.addLimit(bandwidth);
        }

        return bucketBuilder
            .withSynchronizationStrategy(synchronizationStrategy)
            .withMillisecondPrecision()
            .build();
    }

    /**
     * Creates a new Bandwidth.
     *
     * @param bandwidthConfiguration The Bandwidth configuration
     * @return Newly created Bandwidth
     * @since 1.9.0
     */
    private Bandwidth createBandwidth(final Config bandwidthConfiguration) {

        // Create refill object
        final long tokens = bandwidthConfiguration.getLong("refill.tokens");
        final Duration period = bandwidthConfiguration.getDuration("refill.period");
        final String refillStrategy = bandwidthConfiguration.getString("refill.strategy").toUpperCase(Locale.ENGLISH);

        final Refill refill = switch (refillStrategy) {
            case "GREEDY" -> Refill.greedy(tokens, period);
            case "INTERVALLY" -> Refill.intervally(tokens, period);
            case "INTERVALLY_ALIGNED" -> Refill.intervallyAligned(
                tokens,
                period,
                Instant.now().plusMillis(bandwidthConfiguration.getDuration("refill.timeOfFirstRefill", TimeUnit.MILLISECONDS)),
                bandwidthConfiguration.getBoolean("refill.useAdaptiveInitialTokens"));
            default -> throw new BucketTokenException.UnknownRefillStrategy(refillStrategy);
        };

        // Create bandwidth
        final Bandwidth bandwidth = Bandwidth.classic(bandwidthConfiguration.getInt("capacity"), refill);
        if (bandwidth.isIntervallyAligned() && bandwidth.isUseAdaptiveInitialTokens()) {
            return bandwidth
                .withId(bandwidthConfiguration.getString("id"));
        } else {
            return bandwidth
                .withInitialTokens(ConfigurationUtils.getLongOrDefault(bandwidthConfiguration, "refill.initialTokens", bandwidth.getCapacity()))
                .withId(bandwidthConfiguration.getString("id"));
        }
    }
}
