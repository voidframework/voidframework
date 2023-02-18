package dev.voidframework.vfs.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Virtual File Storage: Memory.
 * @since 1.3.0
 */
public class MemoryVirtualFileStorage implements VirtualFileStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryVirtualFileStorage.class);

    private final Map<String, byte[]> storage = new ConcurrentHashMap<>();

    @Override
    public InputStream retrieveFile(final String fileName) {

        final byte[] fileContent = this.storage.getOrDefault(fileName, null);
        if (fileContent == null) {
            return null;
        }

        return new ByteArrayInputStream(fileContent);
    }

    @Override
    public boolean storeFile(final String fileName, final String contentType, final InputStream contentStream) {

        if (contentStream == null) {
            return false;
        }

        try {
            final byte[] fileContent = contentStream.readAllBytes();
            this.storage.put(fileName, fileContent);
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

        return this.storage.remove(fileName) != null;
    }
}
