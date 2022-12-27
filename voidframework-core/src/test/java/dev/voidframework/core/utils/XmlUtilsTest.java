package dev.voidframework.core.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import dev.voidframework.core.exception.XmlException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class XmlUtilsTest {

    private static final Class<SimpleDto> SIMPLE_DTO_CLASS_TYPE = SimpleDto.class;
    private static final JavaType SIMPLE_DTO_JAVA_TYPE = JsonUtils.objectMapper().constructType(SimpleDto.class);

    @Test
    void constructor() throws NoSuchMethodException {

        // Act
        final Constructor<XmlUtils> constructor = XmlUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);

        // Assert
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertEquals("This is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    void toStringFromDocument() throws IOException, ParserConfigurationException, SAXException {

        // Arrange
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        final Document document = documentBuilder.parse(
            new InputSource(
                new StringReader("<fruits><fruit id=\"1\"><name>Apple</name></fruit></fruits>")));

        // Act
        final String xmlAsString = XmlUtils.toString(document);

        // Assert
        Assertions.assertNotNull(xmlAsString);
        Assertions.assertEquals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><fruits><fruit id=\"1\"><name>Apple</name></fruit></fruits>",
            xmlAsString);
    }

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
    void fromXmlByteArrayClassType() {

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
    void fromXmlDocumentClassType() throws IOException, ParserConfigurationException, SAXException {

        // Arrange
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        final Document document = documentBuilder.parse(
            new InputSource(
                new StringReader("<SimpleDto><hello>World!</hello></SimpleDto>")));

        // Act
        final SimpleDto simpleDto = XmlUtils.fromXml(document, SIMPLE_DTO_CLASS_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("World!", simpleDto.hello);
    }

    @Test
    void fromXmlDocumentNullValue() {

        // Act
        final XmlException.FromXmlConversionFailure exception = Assertions.assertThrows(
            XmlException.FromXmlConversionFailure.class,
            () -> XmlUtils.fromXml((Document) null, SIMPLE_DTO_CLASS_TYPE));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("From XML conversion failure", exception.getMessage());
    }

    @Test
    void fromXmlDocumentJavaType() throws IOException, ParserConfigurationException, SAXException {

        // Arrange
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        final Document document = documentBuilder.parse(
            new InputSource(
                new StringReader("<SimpleDto><hello>World!</hello></SimpleDto>")));

        // Act
        final SimpleDto simpleDto = XmlUtils.fromXml(document, SIMPLE_DTO_JAVA_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("World!", simpleDto.hello);
    }

    @Test
    void fromXmlInputStreamClassType() {

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
