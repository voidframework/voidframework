package com.voidframework.web.http.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.voidframework.web.exception.HttpException;
import com.voidframework.web.http.Context;
import com.voidframework.web.http.HttpContentType;
import com.voidframework.web.http.Result;
import com.voidframework.web.http.param.RequestPath;
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

@Singleton
public final class StaticAssetsController implements HttpContentType {

    private final static Logger LOGGER = LoggerFactory.getLogger(StaticAssetsController.class);

    private final boolean runInDevMode;
    private final String baseAssetResourcesDirectory;

    /**
     * Build a new instance;
     *
     * @param configuration The current configuration
     */
    @Inject
    public StaticAssetsController(final Config configuration) {
        this.runInDevMode = configuration.getBoolean("voidframework.core.runInDevMode");
        this.baseAssetResourcesDirectory = configuration.getString("voidframework.web.baseAssetResourcesDirectory");
    }

    /**
     * Retrieve a webjar asset.
     *
     * @param fileName Requested webjar asset file name
     * @return A result containing the requested webjar asset
     * @throws HttpException.NotFound If requested asset does not exist
     */
    public Result webjarAsset(@RequestPath("fileName") final String fileName) {
        if (StringUtils.isBlank(fileName) || fileName.contains("..")) {
            throw new HttpException.NotFound();
        }

        final InputStream inputStream = this.getClass().getResourceAsStream("/META-INF/resources/webjars/" + fileName);
        if (inputStream == null) {
            throw new HttpException.NotFound();
        }

        final String contentType = detectFileContentType(fileName);

        return Result.ok(inputStream, contentType).setHeader("Cache-Control", "public, max-age=3600;");
    }

    /**
     * Retrieve a static asset.
     *
     * @return A result containing the requested static asset
     * @throws HttpException.NotFound If requested asset does not exist
     */
    public Result staticAsset(final Context context) {
        return staticAsset(context.getRequest().getRequestURI());
    }

    /**
     * Retrieve a static asset.
     *
     * @param fileName Requested static asset file name
     * @return A result containing the requested static asset
     * @throws HttpException.NotFound If requested asset does not exist
     */
    public Result staticAsset(@RequestPath("fileName") final String fileName) {
        if (StringUtils.isBlank(fileName) || fileName.contains("..")) {
            throw new HttpException.NotFound();
        }

        InputStream inputStream = null;
        String contentType = null;

        if (this.runInDevMode) {
            // Try to load file directly (don't need application recompilation)
            final Path requestedFilePath = Paths.get(System.getProperty("user.dir"),
                "src",
                "main",
                "resources",
                this.baseAssetResourcesDirectory,
                fileName);

            try {
                inputStream = Files.newInputStream(requestedFilePath);
                contentType = detectFileContentType(requestedFilePath.toString());
            } catch (final IOException ignore) {
            }
        }

        if (inputStream == null) {
            // Try to load file from resources
            final String requestedFileName = Paths.get(File.separator, this.baseAssetResourcesDirectory, fileName).toString();

            inputStream = this.getClass().getResourceAsStream(requestedFileName);
            if (inputStream == null) {
                throw new HttpException.NotFound();
            }

            contentType = detectFileContentType(requestedFileName);
        }


        return Result.ok(inputStream, contentType).setHeader("Cache-Control", "public, max-age=3600;");
    }

    /**
     * Detect file content type.
     *
     * @param fileName The file name with extension
     * @return The detected file content
     */
    private String detectFileContentType(final String fileName) {
        final Tika tika = new Tika();
        String contentType = tika.detect(fileName);

        if (contentType == null) {
            LOGGER.warn("Can't determine Content-Type for '{}', defaulting to '{}'", fileName, APPLICATION_OCTET_STREAM);
            contentType = APPLICATION_OCTET_STREAM;
        }

        return contentType;
    }
}
