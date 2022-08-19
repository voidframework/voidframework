package dev.voidframework.core.helper;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IO utility methods.
 */
public final class IO {

    /**
     * Default constructor.
     */
    private IO() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
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

    /**
     * Closes the given input stream without any exceptions.
     * This is typically used in finally blocks.
     *
     * @param inputStream The input stream to close
     */
    public static void closeWithoutException(final InputStream inputStream) {

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (final IOException ignore) {
                // This exception is not important
            }
        }
    }

    /**
     * Closes the given output stream without any exceptions.
     * This is typically used in finally blocks.
     *
     * @param outputStream The output stream to close
     */
    public static void closeWithoutException(final OutputStream outputStream) {

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (final IOException ignore) {
                // This exception is not important
            }
        }
    }
}
