package dev.voidframework.core.remoteconfiguration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.exception.RemoteConfigurationException;
import dev.voidframework.core.helper.ClassResolver;
import dev.voidframework.core.helper.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Remote configuration loader.
 */
public final class RemoteConfigurationLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteConfigurationLoader.class);

    private static final String CONFIGURATION_KEY_REGISTERED_PROVIDERS = "voidframework.core.remoteConfiguration.providers";
    private static final String PREFIX_CONFIGURATION_KEY_SINGLE_PROVIDER_CFG = "voidframework.core.remoteConfiguration.";

    /**
     * Default constructor.
     */
    private RemoteConfigurationLoader() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Iterates over all declared providers.
     *
     * @param localConfiguration The local configuration
     * @return The configuration retrieved from all declared providers
     */
    public static Config processAllProviders(final Config localConfiguration) {

        final List<String> providerClassPathList = new ArrayList<>();
        if (localConfiguration.hasPath(CONFIGURATION_KEY_REGISTERED_PROVIDERS)) {
            switch (localConfiguration.getValue(CONFIGURATION_KEY_REGISTERED_PROVIDERS).valueType()) {
                case LIST -> providerClassPathList.addAll(
                    localConfiguration
                        .getStringList(CONFIGURATION_KEY_REGISTERED_PROVIDERS)
                        .stream()
                        .map(String::trim)
                        .filter(classpath -> !classpath.isEmpty())
                        .toList()
                );
                case STRING -> {
                    String cleanedClassPath = localConfiguration.getString(CONFIGURATION_KEY_REGISTERED_PROVIDERS);
                    cleanedClassPath = cleanedClassPath.replaceAll("[\\[\\]\"' ]", StringConstants.EMPTY);

                    if (!cleanedClassPath.isEmpty()) {

                        if (cleanedClassPath.contains(StringConstants.COMMA)) {
                            providerClassPathList.addAll(List.of(cleanedClassPath.split(StringConstants.COMMA)));
                        } else {
                            providerClassPathList.add(cleanedClassPath);
                        }
                    }
                }
            }
        }

        if (!providerClassPathList.isEmpty()) {
            final StringBuilder sb = new StringBuilder(512);
            final AtomicInteger keyFetchCount = new AtomicInteger(0);
            final AtomicInteger storedFileCount = new AtomicInteger(0);

            for (final String classPath : providerClassPathList) {
                final Class<?> classType = ClassResolver.forName(classPath);
                if (classType == null) {
                    throw new RemoteConfigurationException.ProviderDoesNotExist(classPath);
                }

                try {
                    final RemoteConfigurationProvider remoteConfigurationProvider = classType
                        .asSubclass(RemoteConfigurationProvider.class)
                        .getConstructor()
                        .newInstance();

                    final Config providerConfiguration = localConfiguration.getConfig(
                        PREFIX_CONFIGURATION_KEY_SINGLE_PROVIDER_CFG + remoteConfigurationProvider.getConfigurationObjectName());
                    if (providerConfiguration == null) {
                        // The current provider is not configured, continue to the next one
                        continue;
                    }

                    remoteConfigurationProvider.loadConfiguration(
                        providerConfiguration,
                        kvObj -> {
                            kvObj.apply(sb);
                            keyFetchCount.incrementAndGet();
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("[{}] {}", remoteConfigurationProvider.getName(), kvObj.toStringWithAdaptativeMask());
                            }
                        },
                        fileObj -> {
                            fileObj.apply();
                            IO.closeWithoutException(fileObj);
                            storedFileCount.incrementAndGet();
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("[{}] Store {}", remoteConfigurationProvider.getName(), fileObj);
                            }
                        }
                    );

                    LOGGER.info(
                        "[{}] {} configuration keys fetched and {} files stored",
                        remoteConfigurationProvider.getName(),
                        keyFetchCount.get(),
                        storedFileCount.get());

                    keyFetchCount.set(0);
                    storedFileCount.set(0);
                } catch (final IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException ex) {
                    throw new RemoteConfigurationException.BadProvider(classPath, ex);
                }
            }

            return ConfigFactory.parseString(sb.toString());
        }

        return ConfigFactory.empty();
    }
}
