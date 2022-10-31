package dev.voidframework.core.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class XmlUtilsTest {

    private static final Class<SimpleDto> SIMPLE_DTO_CLASS_TYPE = SimpleDto.class;
    private static final JavaType SIMPLE_DTO_JAVA_TYPE = JsonUtils.objectMapper().constructType(SimpleDto.class);

    @Test
    void toStringFromObject() {

        // Arrange
        final SimpleDto simpleDto = new SimpleDto("World!");

        // Act
        final String xmlAsString = XmlUtils.toString(simpleDto);

        // Assert
        Assertions.assertNotNull(xmlAsString);
        Assertions.assertEquals("<SimpleDto><hello>World!</hello></SimpleDto>", xmlAsString);
    }

    @Test
    void fromXmlByteArray() {

        // Arrange
        final byte[] xmlAsByteArray = "<SimpleDto><hello>World!</hello></SimpleDto>".getBytes(StandardCharsets.UTF_8);

        // Act
        final SimpleDto simpleDto = XmlUtils.fromXml(xmlAsByteArray, SIMPLE_DTO_CLASS_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("World!", simpleDto.hello);
    }

    @Test
    void fromXmlByteArrayJavaType() {

        // Arrange
        final byte[] xmlAsByteArray = "<SimpleDto><hello>World!</hello></SimpleDto>".getBytes(StandardCharsets.UTF_8);

        // Act
        final SimpleDto simpleDto = XmlUtils.fromXml(xmlAsByteArray, SIMPLE_DTO_JAVA_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("World!", simpleDto.hello);
    }

    @Test
    void fromXmlInputStream() {

        // Arrange
        final InputStream xmlAsInputStream = new ByteArrayInputStream(
            "<SimpleDto><hello>World!</hello></SimpleDto>".getBytes(StandardCharsets.UTF_8));

        // Act
        final SimpleDto simpleDto = XmlUtils.fromXml(xmlAsInputStream, SIMPLE_DTO_CLASS_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("World!", simpleDto.hello);
    }

    @Test
    void fromXmlInputStreamJavaType() {

        // Arrange
        final InputStream xmlAsInputStream = new ByteArrayInputStream(
            "<SimpleDto><hello>World!</hello></SimpleDto>".getBytes(StandardCharsets.UTF_8));

        // Act
        final SimpleDto simpleDto = XmlUtils.fromXml(xmlAsInputStream, SIMPLE_DTO_JAVA_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("World!", simpleDto.hello);
    }

    /**
     * Simple DTO.
     */
    public record SimpleDto(String hello) {

        @JsonCreator
        public SimpleDto(@JsonProperty("hello") final String hello) {
            this.hello = hello;
        }
    }
}
