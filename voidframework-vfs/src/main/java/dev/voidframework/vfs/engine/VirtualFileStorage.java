package dev.voidframework.vfs.engine;

import java.io.InputStream;
import java.util.Properties;

/**
 * Allows manipulation of files without worrying about the type of storage.
 *
 * @since 1.3.0
 */
public interface VirtualFileStorage {

    /**
     * Retrieves a specific file content.
     *
     * @param fileName Name of the file to retrieve
     * @return A stream containing the file content
     * @since 1.3.0
     */
    InputStream retrieveFile(final String fileName);

    /**
     * Stores the given file content on the storage.
     *
     * @param fileName      Name of the file to store
     * @param contentType   Content type (ie: image/png)
     * @param contentStream Stream containing the file content
     * @return {@code true} in case of success, otherwise {@code false}
     * @since 1.3.0
     */
    boolean storeFile(final String fileName,
                      final String contentType,
                      final InputStream contentStream);

    /**
     * Stores the given file content on the storage.
     *
     * @param fileName      Name of the file to store
     * @param contentType   Content type (ie: image/png)
     * @param contentStream Stream containing the file content
     * @param properties    Additional properties to use when storing the file (depends on the used backend)
     * @return {@code true} in case of success, otherwise {@code false}
     * @since 1.3.0
     */
    boolean storeFile(final String fileName,
                      final String contentType,
                      final InputStream contentStream,
                      final Properties properties);

    /**
     * Deletes a specific file.
     *
     * @param fileName Name of the file to delete
     * @return {@code true} in case of success, otherwise {@code false}
     * @since 1.3.0
     */
    boolean deleteFile(final String fileName);
}
