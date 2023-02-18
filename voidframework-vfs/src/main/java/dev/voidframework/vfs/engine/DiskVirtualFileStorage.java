package dev.voidframework.vfs.engine;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

/**
 * Virtual File Storage: Disk.
 *
 * @since 1.3.0
 */
public class DiskVirtualFileStorage implements VirtualFileStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskVirtualFileStorage.class);

    private final Path basePath;

    /**
     * Build a new instance.
     *
     * @param configuration The application configuration
     * @since 1.3.0
     */
    @Inject
    public DiskVirtualFileStorage(final Config configuration) {

        final String storageDirectory;
        if (configuration.hasPath("basePath")) {
            storageDirectory = configuration.getString("basePath");
        } else {
            storageDirectory = System.getProperty("java.io.tmpdir");
        }

        this.basePath = Paths.get(storageDirectory);
    }

    @Override
    public InputStream retrieveFile(final String fileName) {

        final Path filePath = this.basePath.resolve(fileName);
        try {
            return Files.newInputStream(filePath, StandardOpenOption.READ);
        } catch (final IOException ignore) {
            return null;
        }
    }

    @Override
    public boolean storeFile(final String fileName, final String contentType, final InputStream contentStream) {

        if (contentStream == null) {
            return false;
        }

        final Path filePath = this.basePath.resolve(fileName);
        try {
            try (final OutputStream outStream = new FileOutputStream(filePath.toFile())) {
                final byte[] buffer = new byte[8192];
                int nbReadBytes;
                while ((nbReadBytes = contentStream.read(buffer, 0, buffer.length)) != -1) {
                    outStream.write(buffer, 0, nbReadBytes);
                }
                outStream.flush();
            }
            return true;
        } catch (final IOException exception) {
            LOGGER.error("Can't store file %s (%s)".formatted(fileName, contentType), exception);
            return false;
        }
    }

    @Override
    public boolean storeFile(final String fileName,
                             final String contentType,
                             final InputStream contentStream,
                             final Properties properties) {

        return this.storeFile(fileName, contentType, contentStream);
    }

    @Override
    public boolean deleteFile(final String fileName) {

        final Path filePath = this.basePath.resolve(fileName);
        try {
            Files.delete(filePath);
            return true;
        } catch (final IOException ignore) {
            return false;
        }
    }
}
