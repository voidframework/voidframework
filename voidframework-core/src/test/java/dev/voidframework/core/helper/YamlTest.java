package dev.voidframework.core.helper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.charset.StandardCharsets;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class YamlTest {

    @Test
    void fromYamlByteArray() {

        // Arrange
        final byte[] yamlAsByteArray = """
            hello: "world!"
            """.getBytes(StandardCharsets.UTF_8);

        // Act
        final SimpleDto simpleDto = Yaml.fromYaml(yamlAsByteArray, SimpleDto.class);

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
        final SimpleDto simpleDto = Yaml.fromYaml(yamlAsByteArray, null);

        // Assert
        Assertions.assertNull(simpleDto);
    }

    @Test
    void fromYamlJsonNode() {

        // Arrange
        final ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("hello", "world!");

        // Act
        final SimpleDto simpleDto = Yaml.fromYaml(objectNode, SimpleDto.class);

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
        final SimpleDto simpleDto = Yaml.fromYaml(objectNode, null);

        // Assert
        Assertions.assertNull(simpleDto);
    }

    @Test
    void toYamlFromByteArray() {

        // Arrange
        final byte[] yamlAsByteArray = """
            hello: "world!"
            """.getBytes(StandardCharsets.UTF_8);

        // Act
        final JsonNode jsonNode = Yaml.toYaml(yamlAsByteArray);

        // Assert
        Assertions.assertNotNull(jsonNode);
        Assertions.assertEquals("world!", jsonNode.get("hello").asText());
    }

    @Test
    void toStringFromObject() {

        // Arrange
        final SimpleDto simpleDto = new SimpleDto("world!");

        // Act
        final String yamlDocument = Yaml.toString(simpleDto);

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
        final String yamlDocument = Yaml.toString(objectNode);

        // Assert
        Assertions.assertNotNull(yamlDocument);
    }

    /**
     * Simple DTO.
     */
    public static class SimpleDto {

        public final String hello;

        @JsonCreator
        public SimpleDto(@JsonProperty("hello") final String hello) {
            this.hello = hello;
        }
    }
}
