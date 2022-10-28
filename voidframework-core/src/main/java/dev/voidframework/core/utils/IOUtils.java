package dev.voidframework.core.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * IO utility methods.
 */
public final class IOUtils {

    /**
     * Default constructor.
     */
    private IOUtils() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Retrieves the number of bytes available to be read.
     *
     * @param inputStream The input stream
     * @return The number of bytes available to be read
     */
    public static long availableBytes(final InputStream inputStream) {

        if (inputStream == null) {
            return -1;
        }

        try {
            return inputStream.available();
        } catch (final IOException ignore) {
            return -1;
        }
    }

    /**
     * Closes the given closeable without any exceptions.
     * This is typically used in finally blocks.
     *
     * @param closeable The closeable to close
     */
    public static void closeWithoutException(final Closeable closeable) {

        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException ignore) {
                // This exception is not important
            }
        }
    }
}
