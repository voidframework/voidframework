package dev.voidframework.core.remoteconfiguration;

import dev.voidframework.core.helper.Reflection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class FileCfgObjectTest {

    @Test
    void simpleFileContent() throws IOException {

        // Arrange
        final FileCfgObject fileCfgObject = new FileCfgObject("key", "<FILE>./test;SGVsbG8gV29ybGQh");

        // Act
        final String toString = fileCfgObject.toString();

        // Assert
        final InputStream contentInputStream = Reflection.getFieldValue(fileCfgObject, "is", InputStream.class);
        Assertions.assertNotNull(contentInputStream);

        final String content = new String(contentInputStream.readAllBytes(), StandardCharsets.UTF_8);
        Assertions.assertEquals("Hello World!", content);

        Assertions.assertEquals("FileCfgObject[size <- 12 ; target <- ./test]", toString);
    }

    @Test
    void quotedFileContent() throws IOException {

        // Arrange
        final FileCfgObject fileCfgObject = new FileCfgObject("key", "\"<FILE>./test;SGVsbG8gV29ybGQh\"");

        // Act
        final String toString = fileCfgObject.toString();

        // Assert
        final InputStream contentInputStream = Reflection.getFieldValue(fileCfgObject, "is", InputStream.class);
        Assertions.assertNotNull(contentInputStream);

        final String content = new String(contentInputStream.readAllBytes(), StandardCharsets.UTF_8);
        Assertions.assertEquals("Hello World!", content);

        Assertions.assertEquals("FileCfgObject[size <- 12 ; target <- ./test]", toString);
    }

    @Test
    void missingMagic() throws IOException {

        // Arrange
        final FileCfgObject fileCfgObject = new FileCfgObject("key", "./test;SGVsbG8gV29ybGQh");

        // Act
        final String toString = fileCfgObject.toString();

        // Assert
        final InputStream contentInputStream = Reflection.getFieldValue(fileCfgObject, "is", InputStream.class);
        Assertions.assertNotNull(contentInputStream);

        final String content = new String(contentInputStream.readAllBytes(), StandardCharsets.UTF_8);
        Assertions.assertEquals("Hello World!", content);

        Assertions.assertEquals("FileCfgObject[size <- 12 ; target <- ./test]", toString);
    }

    @Test
    void byteArrayFileContent() throws IOException {

        // Arrange
        final byte[] contentAsByteArray = "Hello World!".getBytes(StandardCharsets.UTF_8);
        final FileCfgObject fileCfgObject = new FileCfgObject(contentAsByteArray, "./test");

        // Act
        final String toString = fileCfgObject.toString();

        // Assert
        final InputStream contentInputStream = Reflection.getFieldValue(fileCfgObject, "is", InputStream.class);
        Assertions.assertNotNull(contentInputStream);

        final String content = new String(contentInputStream.readAllBytes(), StandardCharsets.UTF_8);
        Assertions.assertEquals("Hello World!", content);

        Assertions.assertEquals("FileCfgObject[size <- 12 ; target <- ./test]", toString);
    }

    @Test
    void apply() throws IOException {

        // Arrange
        final FileCfgObject fileCfgObject = new FileCfgObject("key", "./test;SGVsbG8gV29ybGQh");

        // Act
        fileCfgObject.apply();
        fileCfgObject.close();

        final File file = new File("./test");
        if (file.exists()) {
            file.deleteOnExit();
        }

        // Assert
        Assertions.assertTrue(file.exists());

        final byte[] readedContentAsByteArray = Files.readAllBytes(file.toPath());
        final String readedContent = new String(readedContentAsByteArray, StandardCharsets.UTF_8);
        Assertions.assertEquals("Hello World!", readedContent);
    }
}
