package dev.voidframework.template.freemarker.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import dev.voidframework.core.utils.VoidFrameworkVersion;
import dev.voidframework.template.exception.TemplateException;
import dev.voidframework.template.freemarker.method.ConfigTemplateMethodModel;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateMethodModelEx;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FreeMarker configuration provider.
 *
 * @since 1.0.0
 */
@Singleton
public class FreeMarkerConfigurationProvider implements Provider<Configuration> {

    private final Config configuration;
    private Configuration freeMarkerConfiguration;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @since 1.0.0
     */
    @Inject
    public FreeMarkerConfigurationProvider(final Config configuration) {

        this.configuration = configuration;
    }

    @Override
    public Configuration get() {

        if (this.freeMarkerConfiguration != null) {
            return this.freeMarkerConfiguration;
        }

        final String basePackagePath = this.configuration.getString("voidframework.template.basePackagePath");

        final DefaultObjectWrapperBuilder defaultObjectWrapperBuilder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_31);
        defaultObjectWrapperBuilder.setExposeFields(true);

        this.freeMarkerConfiguration = new Configuration(Configuration.VERSION_2_3_31);
        this.freeMarkerConfiguration.setDefaultEncoding("UTF-8");
        this.freeMarkerConfiguration.setFallbackOnNullLoopVariable(false);
        this.freeMarkerConfiguration.setLocalizedLookup(false);
        this.freeMarkerConfiguration.setLogTemplateExceptions(false);
        this.freeMarkerConfiguration.setOutputFormat(HTMLOutputFormat.INSTANCE);
        this.freeMarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.freeMarkerConfiguration.setWrapUncheckedExceptions(true);
        this.freeMarkerConfiguration.setObjectWrapper(defaultObjectWrapperBuilder.build());

        if (this.configuration.getBoolean("voidframework.core.runInDevMode")) {
            try {
                final List<Path> viewsDirectoryPathList = resolvePossibleLocations();
                final TemplateLoader[] templateLoaderArray = new TemplateLoader[viewsDirectoryPathList.size() + 1];

                int idx = 0;
                for (final Path viewsDirectoryPath : viewsDirectoryPathList) {
                    templateLoaderArray[idx] = new FileTemplateLoader(viewsDirectoryPath.toFile());
                    idx += 1;
                }
                templateLoaderArray[idx] = new ClassTemplateLoader(this.getClass(), basePackagePath);

                this.freeMarkerConfiguration.setCacheStorage(new freemarker.cache.MruCacheStorage(0, 0));
                this.freeMarkerConfiguration.setClassForTemplateLoading(this.getClass(), basePackagePath);
                this.freeMarkerConfiguration.setTemplateLoader(new MultiTemplateLoader(templateLoaderArray));
            } catch (final IOException e) {
                throw new TemplateException.TemplateEngineInitFailure(e);
            }
        } else {
            this.freeMarkerConfiguration.setCacheStorage(new freemarker.cache.MruCacheStorage(20, Integer.MAX_VALUE));
            this.freeMarkerConfiguration.setClassForTemplateLoading(this.getClass(), basePackagePath);
            this.freeMarkerConfiguration.setTemplateUpdateDelayMilliseconds(Integer.MAX_VALUE);
        }

        this.freeMarkerConfiguration.setSharedVariable("voidFrameworkVersion", new SimpleScalar(VoidFrameworkVersion.getVersion()));
        this.freeMarkerConfiguration.setSharedVariable("config", new ConfigTemplateMethodModel(this.configuration));
        this.freeMarkerConfiguration.setSharedVariable("isDevMode", this.configuration.getBoolean("voidframework.core.runInDevMode")
            ? TemplateBooleanModel.TRUE
            : TemplateBooleanModel.FALSE);
        this.freeMarkerConfiguration.setSharedVariable("displaySize", (TemplateMethodModelEx) args ->
            new SimpleScalar(FileUtils.byteCountToDisplaySize(((SimpleNumber) args.get(0)).getAsNumber().longValue())));

        return this.freeMarkerConfiguration;
    }

    /**
     * Resolve possible template locations.
     *
     * @return The possible template locations
     * @since 1.0.0
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
