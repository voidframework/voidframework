package dev.voidframework.core.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.voidframework.core.exception.YamlException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class YamlUtilsTest {

    private static final Class<SimpleDto> SIMPLE_DTO_CLASS_TYPE = SimpleDto.class;
    private static final JavaType SIMPLE_DTO_JAVA_TYPE = JsonUtils.objectMapper().constructType(SimpleDto.class);

    @Test
    void constructor() throws NoSuchMethodException {

        // Act
        final Constructor<YamlUtils> constructor = YamlUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);

        // Assert
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertEquals("This is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    void fromYamlByteArray() {

        // Arrange
        final byte[] yamlAsByteArray = """
            hello: "world!"
            """.getBytes(StandardCharsets.UTF_8);

        // Act
        final SimpleDto simpleDto = YamlUtils.fromYaml(yamlAsByteArray, SIMPLE_DTO_CLASS_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world!", simpleDto.hello);
    }

    @Test
    void fromYamlByteArrayJavaType() {

        // Arrange
        final byte[] yamlAsByteArray = """
            hello: "world!"
            """.getBytes(StandardCharsets.UTF_8);

        // Act
        final SimpleDto simpleDto = YamlUtils.fromYaml(yamlAsByteArray, SIMPLE_DTO_JAVA_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world!", simpleDto.hello);
    }

    @Test
    void fromYamlInputStream() {

        // Arrange
        final InputStream yamlAsInputStream = new ByteArrayInputStream("""
            hello: "world!"
            """.getBytes(StandardCharsets.UTF_8));

        // Act
        final SimpleDto simpleDto = YamlUtils.fromYaml(yamlAsInputStream, SIMPLE_DTO_CLASS_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world!", simpleDto.hello);
    }

    @Test
    void fromYamlInputStreamJavaType() {

        // Arrange
        final InputStream yamlAsInputStream = new ByteArrayInputStream("""
            hello: "world!"
            """.getBytes(StandardCharsets.UTF_8));

        // Act
        final SimpleDto simpleDto = YamlUtils.fromYaml(yamlAsInputStream, SIMPLE_DTO_JAVA_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world!", simpleDto.hello);
    }

    @Test
    void fromYamlByteArrayInvalidClassType() {

        // Arrange
        final byte[] yamlAsByteArray = """
            hello: "world!"
            """.getBytes(StandardCharsets.UTF_8);

        // Act
        final YamlException.FromYamlConversionFailure exception = Assertions.assertThrows(
            YamlException.FromYamlConversionFailure.class,
            () -> YamlUtils.fromYaml(yamlAsByteArray, (JavaType) null));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("From YAML conversion failure", exception.getMessage());
    }

    @Test
    void fromYamlJsonNode() {

        // Arrange
        final ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("hello", "world!");

        // Act
        final SimpleDto simpleDto = YamlUtils.fromYaml(objectNode, SIMPLE_DTO_CLASS_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world!", simpleDto.hello);
    }

    @Test
    void fromYamlJsonNodeJavaType() {

        // Arrange
        final ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("hello", "world!");

        // Act
        final SimpleDto simpleDto = YamlUtils.fromYaml(objectNode, SIMPLE_DTO_JAVA_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world!", simpleDto.hello);
    }

    @Test
    void fromYamlJsonNodeInvalidClassType() {

        // Arrange
        final ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("hello", "world!");

        // Act
        final YamlException.FromYamlConversionFailure exception = Assertions.assertThrows(
            YamlException.FromYamlConversionFailure.class,
            () -> YamlUtils.fromYaml(objectNode, (Class<?>) null));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("From YAML conversion failure", exception.getMessage());
    }

    @Test
    void toYamlFromByteArray() {

        // Arrange
        final byte[] yamlAsByteArray = """
            hello: "world!"
            """.getBytes(StandardCharsets.UTF_8);

        // Act
        final JsonNode jsonNode = YamlUtils.toYaml(yamlAsByteArray);

        // Assert
        Assertions.assertNotNull(jsonNode);
        Assertions.assertEquals("world!", jsonNode.get("hello").asText());
    }

    @Test
    void toYamlFromInputStream() {

        // Arrange
        final InputStream yamlAsInputStream = new ByteArrayInputStream("""
            hello: "world!"
            """.getBytes(StandardCharsets.UTF_8));

        // Act
        final JsonNode jsonNode = YamlUtils.toYaml(yamlAsInputStream);

        // Assert
        Assertions.assertNotNull(jsonNode);
        Assertions.assertEquals("world!", jsonNode.get("hello").asText());
    }

    @Test
    void toStringFromObject() {

        // Arrange
        final SimpleDto simpleDto = new SimpleDto("world!");

        // Act
        final String yamlDocument = YamlUtils.toString(simpleDto);

        // Assert
        Assertions.assertNotNull(yamlDocument);
        Assertions.assertEquals("""
            ---
            hello: "world!"
            """, yamlDocument);
    }

    @Test
    void toStringFromObjectNode() {

        // Arrange
        final ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("hello", "world!");

        // Act
        final String yamlDocument = YamlUtils.toString(objectNode);

        // Assert
        Assertions.assertNotNull(yamlDocument);
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
