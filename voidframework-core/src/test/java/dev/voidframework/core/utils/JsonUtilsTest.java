package dev.voidframework.core.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.voidframework.core.exception.JsonException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class JsonUtilsTest {

    @Test
    void objectMapper() {

        // Act
        final ObjectMapper objectMapper = JsonUtils.objectMapper();

        // Assert
        Assertions.assertNotNull(objectMapper);
    }

    @Test
    void toStringFromJsonNode() {

        // Arrange
        final ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("hello", "world");

        // Act
        final String jsonAsString = JsonUtils.toString(objectNode);

        // Assert
        Assertions.assertNotNull(jsonAsString);
        Assertions.assertEquals("{\"hello\":\"world\"}", jsonAsString);
    }

    @Test
    void toJsonFromObject() {

        // Arrange
        final Map<String, String> dataMap = new HashMap<>();
        dataMap.put("hello", "world");

        // Act
        final JsonNode jsonNode = JsonUtils.toJson(dataMap);

        // Assert
        Assertions.assertNotNull(jsonNode);
        Assertions.assertTrue(jsonNode.hasNonNull("hello"));
        Assertions.assertTrue(jsonNode.get("hello").isTextual());
        Assertions.assertEquals("world", jsonNode.get("hello").asText());
    }

    @Test
    void toJsonFromByteArray() {

        // Arrange
        final byte[] jsonAsByteArray = "{\"hello\": \"world\"}".getBytes(StandardCharsets.UTF_8);

        // Act
        final JsonNode jsonNode = JsonUtils.toJson(jsonAsByteArray);

        // Assert
        Assertions.assertNotNull(jsonNode);
        Assertions.assertTrue(jsonNode.hasNonNull("hello"));
        Assertions.assertTrue(jsonNode.get("hello").isTextual());
        Assertions.assertEquals("world", jsonNode.get("hello").asText());
    }

    @Test
    void toMap() {

        // Arrange
        final SimpleDto simpleDto = new SimpleDto("world!");

        // Act
        final Map<String, Object> dataMap = JsonUtils.toMap(simpleDto);

        // Assert
        Assertions.assertNotNull(dataMap);
        Assertions.assertFalse(dataMap.isEmpty());
        Assertions.assertEquals("world!", dataMap.get("hello"));
    }

    @Test
    void toMapInvalidData() {

        // Arrange
        final int invalidDataNotObject = -1;

        // Act
        final Map<String, Object> dataMap = JsonUtils.toMap(invalidDataNotObject);

        // Assert
        Assertions.assertNull(dataMap);
    }

    @Test
    void toJsonFromByteArrayInvalidData() {

        // Arrange
        final byte[] jsonAsByteArray = "{\"hello: world\"}".getBytes(StandardCharsets.UTF_8);

        // Act
        final JsonException.ToJsonConversionFailure exception = Assertions.assertThrows(
            JsonException.ToJsonConversionFailure.class,
            () -> JsonUtils.toJson(jsonAsByteArray));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("To JSON conversion failure", exception.getMessage());
    }

    @Test
    void fromJsonByteArray() {

        // Arrange
        final byte[] jsonAsByteArray = "{\"hello\": \"world\"}".getBytes(StandardCharsets.UTF_8);

        // Act
        final SimpleDto simpleDto = JsonUtils.fromJson(jsonAsByteArray, SimpleDto.class);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world", simpleDto.hello);
    }

    @Test
    void fromJsonByteArrayInvalidData() {

        // Arrange
        final byte[] jsonAsByteArray = "{\"hello: world\"}".getBytes(StandardCharsets.UTF_8);

        // Act
        final JsonException.FromJsonConversionFailure exception = Assertions.assertThrows(
            JsonException.FromJsonConversionFailure.class,
            () -> JsonUtils.fromJson(jsonAsByteArray, SimpleDto.class));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("From JSON conversion failure", exception.getMessage());
    }

    @Test
    void fromJsonString() {

        // Arrange
        final String jsonAsString = "{\"hello\": \"world\"}";

        // Act
        final SimpleDto simpleDto = JsonUtils.fromJson(jsonAsString, SimpleDto.class);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world", simpleDto.hello);
    }

    @Test
    void fromJsonStringInvalidData() {

        // Arrange
        final String jsonAsString = "{\"hello: world\"}";

        // Act
        final JsonException.FromJsonConversionFailure exception = Assertions.assertThrows(
            JsonException.FromJsonConversionFailure.class,
            () -> JsonUtils.fromJson(jsonAsString, SimpleDto.class));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("From JSON conversion failure", exception.getMessage());
    }

    @Test
    void fromJsonJsonNode() {

        // Arrange
        final ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("hello", "world");

        // Act
        final SimpleDto simpleDto = JsonUtils.fromJson(objectNode, SimpleDto.class);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world", simpleDto.hello);
    }

    @Test
    void fromJsonJsonNodeInvalidClassType() {

        // Arrange
        final ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("hello", "world");

        // Act
        final JsonException.FromJsonConversionFailure exception = Assertions.assertThrows(
            JsonException.FromJsonConversionFailure.class,
            () -> JsonUtils.fromJson(objectNode, (Class<? extends SimpleDto>) null));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("From JSON conversion failure", exception.getMessage());
    }

    @Test
    void fromMap() {

        // Arrange
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("hello", "world!");

        // Act
        final SimpleDto simpleDto = JsonUtils.fromMap(dataMap, SimpleDto.class);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world!", simpleDto.hello);
    }

    @Test
    void fromMapInvalidClassType() {

        // Arrange
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("hello", "world!");

        // Act
        final SimpleDto simpleDto = JsonUtils.fromMap(dataMap, (JavaType) null);

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
