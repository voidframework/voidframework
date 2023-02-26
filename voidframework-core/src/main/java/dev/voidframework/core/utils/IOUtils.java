package dev.voidframework.core.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * IO utility methods.
 *
 * @since 1.2.0
 */
public final class IOUtils {

    /**
     * Default constructor.
     *
     * @since 1.2.0
     */
    private IOUtils() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Retrieves the number of bytes available to be read.
     *
     * @param inputStream The input stream
     * @return The number of bytes available to be read
     * @since 1.3.0
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
     * @since 1.2.0
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

    /**
     * Reset a stream without any exceptions.
     *
     * @param inputStream The input stream to reset
     * @since 1.3.0
     */
    public static void resetWithoutException(final InputStream inputStream) {

        if (inputStream != null) {
            try {
                inputStream.reset();
            } catch (final IOException ignore) {
                // This exception is not important
            }
        }
    }
}
