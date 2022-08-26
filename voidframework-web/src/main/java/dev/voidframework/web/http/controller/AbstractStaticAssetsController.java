package dev.voidframework.web.http.controller;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.web.exception.HttpException;
import dev.voidframework.web.http.Context;
import dev.voidframework.web.http.HttpContentType;
import dev.voidframework.web.http.HttpMethod;
import dev.voidframework.web.http.Result;
import dev.voidframework.web.http.annotation.NoCSRF;
import dev.voidframework.web.http.annotation.RequestPath;
import dev.voidframework.web.http.annotation.RequestRoute;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Abstraction to facilitate the use of static and webjar assets.
 */
public abstract class AbstractStaticAssetsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStaticAssetsController.class);

    private final boolean runInDevMode;
    private final String baseAssetResourcesDirectory;
    private final Tika tika;


    /**
     * Build a new instance;
     *
     * @param configuration The application configuration
     */
    @Inject
    protected AbstractStaticAssetsController(final Config configuration) {

        this.runInDevMode = configuration.getBoolean("voidframework.core.runInDevMode");
        this.baseAssetResourcesDirectory = configuration.getString("voidframework.web.baseAssetResourcesDirectory");

        this.tika = new Tika();
    }

    /**
     * Retrieves a webjar asset.
     *
     * @param fileName Requested webjar asset file name
     * @return A result containing the requested webjar asset
     * @throws HttpException.NotFound If requested asset does not exist
     */
    @NoCSRF
    @RequestRoute(method = HttpMethod.GET, route = "/webjars/(?<fileName>.*)")
    @SuppressWarnings("unused")
    public Result webjarAsset(@RequestPath("fileName") final String fileName) {

        if (StringUtils.isBlank(fileName) || fileName.contains(StringConstants.DOUBLE_DOT)) {
            throw new HttpException.NotFound();
        }

        final InputStream inputStream = this.getClass().getResourceAsStream("/META-INF/resources/webjars/" + fileName);
        if (inputStream == null) {
            throw new HttpException.NotFound();
        }

        final String contentType = detectFileContentType(fileName);

        return Result.ok(inputStream, contentType).withHeader("Cache-Control", "public, max-age=3600;");
    }

    /**
     * Retrieves a static asset.
     *
     * @param context The current context
     * @return A result containing the requested static asset
     * @throws HttpException.NotFound If requested asset does not exist
     */
    @NoCSRF
    @RequestRoute(method = HttpMethod.GET, route = "/(favicon.ico|robots.txt)")
    @SuppressWarnings("unused")
    public Result staticAsset(final Context context) {

        return staticAsset(context.getRequest().getRequestURI());
    }

    /**
     * Retrieves a static asset.
     *
     * @param fileName Requested static asset file name
     * @return A result containing the requested static asset
     * @throws HttpException.NotFound If requested asset does not exist
     */
    @NoCSRF
    @RequestRoute(method = HttpMethod.GET, route = "/static/(?<fileName>.*)")
    public Result staticAsset(@RequestPath("fileName") final String fileName) {

        if (StringUtils.isBlank(fileName) || fileName.contains(StringConstants.DOUBLE_DOT)) {
            throw new HttpException.NotFound();
        }

        InputStream inputStream = null;
        String contentType = null;

        if (this.runInDevMode) {
            // Try to load file directly (don't need application recompilation)
            final Path fileLocation = resolveLocation(fileName);
            if (fileLocation != null) {
                try {
                    inputStream = Files.newInputStream(fileLocation);
                    contentType = detectFileContentType(fileName);
                } catch (final IOException ignore) {
                    // This exception is not important, we continue the process as if nothing had happened
                }
            }
        }

        if (inputStream == null) {
            // Try to load file from resources
            String requestedFileName = Paths.get(this.baseAssetResourcesDirectory, fileName).toString();
            if (requestedFileName.charAt(0) != File.separatorChar) {
                requestedFileName = File.separator + requestedFileName;
            }

            inputStream = this.getClass().getResourceAsStream(requestedFileName.replace(File.separator, StringConstants.SLASH));
            if (inputStream == null) {
                throw new HttpException.NotFound();
            }

            contentType = detectFileContentType(requestedFileName);
        }


        return Result.ok(inputStream, contentType).withHeader("Cache-Control", "public, max-age=3600;");
    }

    /**
     * Detects file content type.
     *
     * @param fileName The file name with extension
     * @return The detected file content
     */
    private String detectFileContentType(final String fileName) {

        String contentType = tika.detect(fileName);

        if (contentType == null) {
            LOGGER.warn("Can't determine Content-Type for '{}', defaulting to '{}'", fileName, HttpContentType.APPLICATION_OCTET_STREAM);
            contentType = HttpContentType.APPLICATION_OCTET_STREAM;
        }

        return contentType;
    }

    /**
     * Resolves file location.
     *
     * @param fileName File name
     * @return The file location, otherwise, null
     */
    private Path resolveLocation(final String fileName) {

        final Path rootPath = Paths.get(System.getProperty("user.dir"));
        final Path resolvePath = Path.of("src", "main", "resources", this.baseAssetResourcesDirectory, fileName);

        final Path firstPossibleLocation = rootPath.resolve(resolvePath);
        if (firstPossibleLocation.toFile().exists()) {
            return firstPossibleLocation;
        }

        try (final Stream<Path> stream = Files.walk(rootPath, 1)) {
            return stream
                .filter(Files::isDirectory)
                .map(Path::getFileName)
                .map(path -> path.resolve(resolvePath))
                .filter(Files::exists)
                .findFirst()
                .orElse(null);
        } catch (final IOException ignore) {
            return null;
        }
    }
}
