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
import java.util.HashMap;
import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class JsonTest {

    @Test
    public void objectMapper() {

        // Act
        final ObjectMapper objectMapper = Json.objectMapper();

        // Assert
        Assertions.assertNotNull(objectMapper);
    }

    @Test
    public void toStringFromJsonNode() {

        // Arrange
        final ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("hello", "world");

        // Act
        final String jsonAsString = Json.toString(objectNode);

        // Assert
        Assertions.assertNotNull(jsonAsString);
        Assertions.assertEquals("{\"hello\":\"world\"}", jsonAsString);
    }

    @Test
    public void toJsonFromObject() {

        // Arrange
        final Map<String, String> dataMap = new HashMap<>();
        dataMap.put("hello", "world");

        // Act
        final JsonNode jsonNode = Json.toJson(dataMap);

        // Assert
        Assertions.assertNotNull(jsonNode);
        Assertions.assertTrue(jsonNode.hasNonNull("hello"));
        Assertions.assertTrue(jsonNode.get("hello").isTextual());
        Assertions.assertEquals("world", jsonNode.get("hello").asText());
    }

    @Test
    public void toJsonFromByteArray() {

        // Arrange
        final byte[] jsonAsByteArray = "{\"hello\": \"world\"}".getBytes(StandardCharsets.UTF_8);

        // Act
        final JsonNode jsonNode = Json.toJson(jsonAsByteArray);

        // Assert
        Assertions.assertNotNull(jsonNode);
        Assertions.assertTrue(jsonNode.hasNonNull("hello"));
        Assertions.assertTrue(jsonNode.get("hello").isTextual());
        Assertions.assertEquals("world", jsonNode.get("hello").asText());
    }

    @Test
    public void toMap() {

        // Arrange
        final SimpleDto simpleDto = new SimpleDto("world!");

        // Act
        final Map<String, Object> dataMap = Json.toMap(simpleDto);

        // Assert
        Assertions.assertNotNull(dataMap);
        Assertions.assertFalse(dataMap.isEmpty());
        Assertions.assertEquals("world!", dataMap.get("hello"));
    }

    @Test
    public void toMapInvalidData() {

        // Arrange
        final int invalidDataNotObject = -1;

        // Act
        final Map<String, Object> dataMap = Json.toMap(invalidDataNotObject);

        // Assert
        Assertions.assertNull(dataMap);
    }

    @Test
    public void toJsonFromByteArrayInvalidData() {

        // Arrange
        final byte[] jsonAsByteArray = "{\"hello: world\"}".getBytes(StandardCharsets.UTF_8);

        // Act
        final JsonNode jsonNode = Json.toJson(jsonAsByteArray);

        // Assert
        Assertions.assertNull(jsonNode);
    }

    @Test
    public void fromJsonByteArray() {

        // Arrange
        final byte[] jsonAsByteArray = "{\"hello\": \"world\"}".getBytes(StandardCharsets.UTF_8);

        // Act
        final SimpleDto simpleDto = Json.fromJson(jsonAsByteArray, SimpleDto.class);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world", simpleDto.hello);
    }

    @Test
    public void fromJsonByteArrayInvalidData() {

        // Arrange
        final byte[] jsonAsByteArray = "{\"hello: world\"}".getBytes(StandardCharsets.UTF_8);

        // Act
        final SimpleDto simpleDto = Json.fromJson(jsonAsByteArray, SimpleDto.class);

        // Assert
        Assertions.assertNull(simpleDto);
    }

    @Test
    public void fromJsonString() {

        // Arrange
        final String jsonAsString = "{\"hello\": \"world\"}";

        // Act
        final SimpleDto simpleDto = Json.fromJson(jsonAsString, SimpleDto.class);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world", simpleDto.hello);
    }

    @Test
    public void fromJsonStringInvalidData() {

        // Arrange
        final String jsonAsString = "{\"hello: world\"}";

        // Act
        final SimpleDto simpleDto = Json.fromJson(jsonAsString, SimpleDto.class);

        // Assert
        Assertions.assertNull(simpleDto);
    }

    @Test
    public void fromJsonJsonNode() {

        // Arrange
        final ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("hello", "world");

        // Act
        final SimpleDto simpleDto = Json.fromJson(objectNode, SimpleDto.class);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world", simpleDto.hello);
    }

    @Test
    public void fromJsonJsonNodeInvalidClassType() {

        // Arrange
        final ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("hello", "world");

        // Act
        final SimpleDto simpleDto = Json.fromJson(objectNode, (Class<? extends SimpleDto>) null);

        // Assert
        Assertions.assertNull(simpleDto);
    }

    @Test
    public void fromMap() {

        // Arrange
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("hello", "world!");

        // Act
        final SimpleDto simpleDto = Json.fromMap(dataMap, SimpleDto.class);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world!", simpleDto.hello);
    }

    @Test
    public void fromMapInvalidClassType() {

        // Arrange
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("hello", "world!");

        // Act
        final SimpleDto simpleDto = Json.fromMap(dataMap, null);

        // Assert
        Assertions.assertNull(simpleDto);
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
