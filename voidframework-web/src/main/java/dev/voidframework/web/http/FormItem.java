package dev.voidframework.web.http;

import java.io.InputStream;

/**
 * A Http form item.
 *
 * @param value       The simple string value. {@code null} if this item represent an uploaded file
 * @param charset     The charset of the simple string value
 * @param isFile      {@code true} if this item represent an uploaded file
 * @param fileSize    The file size. {@code -1} if this item not represent an uploaded file
 * @param inputStream The input stream. {@code null} if this item not represent an uploaded file
 * @since 1.0.0
 */
public record FormItem(String value,
                       String charset,
                       boolean isFile,
                       long fileSize,
                       InputStream inputStream) {
}
