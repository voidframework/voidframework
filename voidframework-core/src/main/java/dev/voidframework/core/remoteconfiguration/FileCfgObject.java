package dev.voidframework.core.remoteconfiguration;

import dev.voidframework.core.constant.CharConstants;
import dev.voidframework.core.constant.StringConstants;
import dev.voidframework.core.exception.RemoteConfigurationException;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Represents a file.
 */
public final class FileCfgObject implements Closeable {

    static final String FILE_MAGIC_ID = "<FILE>";

    private final InputStream is;
    private final String target;
    private int size;

    /**
     * Build a new instance. The file instructions must follows
     * this format: "TARGET;BASE64_CONTENT"
     *
     * @param key        The configuration key
     * @param rawContent The file instructions
     */
    public FileCfgObject(final String key, final String rawContent) {

        final String[] data;
        if (rawContent.charAt(0) == CharConstants.DOUBLE_QUOTE && rawContent.charAt(rawContent.length() - 1) == CharConstants.DOUBLE_QUOTE) {
            data = rawContent
                .substring(1, rawContent.length() - 1)
                .substring(FileCfgObject.FILE_MAGIC_ID.length())
                .split(StringConstants.SEMICOLON);
        } else if (rawContent.startsWith(FileCfgObject.FILE_MAGIC_ID)) {
            data = rawContent
                .substring(FileCfgObject.FILE_MAGIC_ID.length())
                .split(StringConstants.SEMICOLON);
        } else {
            data = rawContent.split(StringConstants.SEMICOLON);
        }

        try {
            final byte[] decodedData = Base64.getDecoder().decode(data[1]);
            this.is = new ByteArrayInputStream(decodedData);
            this.target = data[0].trim();
            this.size = decodedData.length;
        } catch (final IllegalArgumentException | IndexOutOfBoundsException ex) {
            throw new RemoteConfigurationException.BadValue(key, ex.getMessage());
        }
    }

    /**
     * Build a new instance.
     *
     * @param content The file content
     * @param target  Where to save the file
     */
    public FileCfgObject(final byte[] content, final String target) {

        this(new ByteArrayInputStream(content), target);
    }

    /**
     * Build a new instance.
     *
     * @param is     The file input stream
     * @param target Where to save the file
     */
    public FileCfgObject(final InputStream is, final String target) {

        this.is = is;
        this.target = target.trim();
        try {
            this.size = is.available();
        } catch (final IOException ignore) {
            this.size = 0;
        }
    }

    /**
     * Tries to save file.
     */
    public void apply() {

        final File outputFile = new File(this.target);
        try {
            final FileOutputStream fos = new FileOutputStream(outputFile);
            final byte[] buffer = new byte[128];
            int bytesRead;
            while ((bytesRead = this.is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush();
            fos.close();
        } catch (final IOException ex) {
            throw new RemoteConfigurationException.StorageException(this.target, ex.getMessage());
        }
    }

    @Override
    public void close() throws IOException {

        if (this.is != null) {
            this.is.close();
        }
    }

    @Override
    public String toString() {

        return FileCfgObject.class.getSimpleName()
            + "[size <- "
            + this.size
            + " ; target <- "
            + this.target + StringConstants.SQUARE_BRACKET_CLOSE;
    }
}
