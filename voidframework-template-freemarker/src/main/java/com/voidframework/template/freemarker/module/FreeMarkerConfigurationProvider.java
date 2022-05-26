package com.voidframework.template.freemarker.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.typesafe.config.Config;
import com.voidframework.core.helper.VoidFrameworkVersion;
import com.voidframework.template.freemarker.method.ConfigTemplateMethodModel;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FreeMarkerConfigurationProvider implements Provider<Configuration> {

    private final Config configuration;

    @Inject
    public FreeMarkerConfigurationProvider(final Config configuration) {
        this.configuration = configuration;
    }

    @Override
    public Configuration get() {
        final Configuration freeMarkerConfiguration = new Configuration(Configuration.VERSION_2_3_31);
        freeMarkerConfiguration.setDefaultEncoding("UTF-8");
        freeMarkerConfiguration.setFallbackOnNullLoopVariable(false);
        freeMarkerConfiguration.setLocalizedLookup(false);
        freeMarkerConfiguration.setLogTemplateExceptions(false);
        freeMarkerConfiguration.setOutputFormat(HTMLOutputFormat.INSTANCE);
        freeMarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freeMarkerConfiguration.setWrapUncheckedExceptions(true);

        if (this.configuration.getBoolean("voidframework.core.runInDevMode")) {
            try {
                final List<Path> viewsDirectoryPathList = resolvePossibleLocations();
                final TemplateLoader[] templateLoaderArray = new TemplateLoader[viewsDirectoryPathList.size() + 1];

                int idx = 0;
                for (final Path viewsDirectoryPath : viewsDirectoryPathList) {
                    templateLoaderArray[idx] = new FileTemplateLoader(viewsDirectoryPath.toFile());
                    idx += 1;
                }
                templateLoaderArray[idx] = new ClassTemplateLoader(this.getClass(), "/views/");

                freeMarkerConfiguration.setCacheStorage(new freemarker.cache.MruCacheStorage(0, 0));
                freeMarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/views/");
                freeMarkerConfiguration.setTemplateLoader(new MultiTemplateLoader(templateLoaderArray));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            freeMarkerConfiguration.setCacheStorage(new freemarker.cache.MruCacheStorage(20, Integer.MAX_VALUE));
            freeMarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/views/");
            freeMarkerConfiguration.setTemplateUpdateDelayMilliseconds(Integer.MAX_VALUE);
        }

        freeMarkerConfiguration.setSharedVariable("voidFrameworkVersion", new SimpleScalar(VoidFrameworkVersion.getVersion()));
        freeMarkerConfiguration.setSharedVariable("config", new ConfigTemplateMethodModel(this.configuration));

        return freeMarkerConfiguration;
    }

    /**
     * Resolve possible template locations.
     *
     * @return The possible template locations
     */
    private List<Path> resolvePossibleLocations() {
        final Path rootPath = Paths.get(System.getProperty("user.dir"));
        final Path resolvePath = Path.of("src", "main", "resources", "views");

        final Path firstPossibleLocation = rootPath.resolve(resolvePath);
        if (firstPossibleLocation.toFile().exists()) {
            return Collections.singletonList(firstPossibleLocation);
        }

        try (final Stream<Path> stream = Files.walk(rootPath, 1)) {
            return stream
                .filter(Files::isDirectory)
                .map(Path::getFileName)
                .map(path -> path.resolve(resolvePath))
                .filter(Files::exists)
                .collect(Collectors.toList());
        } catch (final IOException ignore) {
            return Collections.emptyList();
        }
    }
}
