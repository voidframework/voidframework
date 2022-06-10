package dev.voidframework.web.http;

import java.io.InputStream;

/**
 * A Http form item.
 */
public record FormItem(String value,
                       String charset,
                       boolean isFile,
                       InputStream inputStream) {
}