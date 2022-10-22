package dev.voidframework.vfs.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.utils.ClassResolverUtils;
import dev.voidframework.vfs.engine.VirtualFileStorage;
import dev.voidframework.vfs.exception.VirtualFileStorageException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Virtual File Storage Module.
 */
public final class VirtualFileStorageModule extends AbstractModule {

    private final Config configuration;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     */
    public VirtualFileStorageModule(final Config configuration) {

        this.configuration = configuration;
    }

    @Override
    protected void configure() {

        final Set<String> vfsConfigurationNameSet = this.configuration.getConfig("voidframework.vfs").entrySet()
            .stream()
            .map(Map.Entry::getKey)
            .map(key -> {
                if (key.contains(StringConstants.DOT)) {
                    return key.substring(0, key.indexOf(StringConstants.DOT));
                } else {
                    return key;
                }
            })
            .collect(Collectors.toSet());

        if (vfsConfigurationNameSet.isEmpty()) {
            throw new VirtualFileStorageException.NotConfigured();
        }

        for (final String vfsConfigurationName : vfsConfigurationNameSet) {
            final Config engineConfiguration = this.configuration.getConfig("voidframework.vfs." + vfsConfigurationName);

            final String engineClassName = engineConfiguration.getString("className");
            final boolean useAsDefault = engineConfiguration.getBoolean("default");

            final Class<? extends VirtualFileStorage> classType = ClassResolverUtils.forName(engineClassName);
            if (classType == null) {
                throw new VirtualFileStorageException.EngineNotFound(engineClassName);
            }

            final Provider<VirtualFileStorage> provider = new VirtualFileStorageProvider(classType, engineConfiguration);
            bind(VirtualFileStorage.class).annotatedWith(Names.named(vfsConfigurationName)).toProvider(provider);
            if (useAsDefault) {
                bind(VirtualFileStorage.class).toProvider(provider);
            }
        }
    }
}
