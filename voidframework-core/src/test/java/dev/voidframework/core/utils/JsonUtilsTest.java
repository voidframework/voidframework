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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
final class JsonUtilsTest {

    private static final Class<SimpleDto> SIMPLE_DTO_CLASS_TYPE = SimpleDto.class;
    private static final JavaType SIMPLE_DTO_JAVA_TYPE = JsonUtils.objectMapper().constructType(SimpleDto.class);

    @Test
    void constructor() throws NoSuchMethodException {

        // Act
        final Constructor<JsonUtils> constructor = JsonUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);

        // Assert
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertEquals("This is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

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
    void toJsonFromInputStream() {

        // Arrange
        final InputStream jsonAsInputstream = new ByteArrayInputStream(
            "{\"hello\": \"world\"}".getBytes(StandardCharsets.UTF_8));

        // Act
        final JsonNode jsonNode = JsonUtils.toJson(jsonAsInputstream);

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
        final SimpleDto simpleDto = JsonUtils.fromJson(jsonAsByteArray, SIMPLE_DTO_CLASS_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world", simpleDto.hello);
    }

    @Test
    void fromJsonByteArrayJavaType() {

        // Arrange
        final byte[] jsonAsByteArray = "{\"hello\": \"world\"}".getBytes(StandardCharsets.UTF_8);

        // Act
        final SimpleDto simpleDto = JsonUtils.fromJson(jsonAsByteArray, SIMPLE_DTO_JAVA_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world", simpleDto.hello);
    }

    @Test
    void fromJsonInputStream() {

        // Arrange
        final InputStream jsonAsInputStream = new ByteArrayInputStream(
            "{\"hello\": \"world\"}".getBytes(StandardCharsets.UTF_8));

        // Act
        final SimpleDto simpleDto = JsonUtils.fromJson(jsonAsInputStream, SIMPLE_DTO_CLASS_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world", simpleDto.hello);
    }

    @Test
    void fromJsonInputStreamJavaType() {

        // Arrange
        final InputStream jsonAsInputStream = new ByteArrayInputStream(
            "{\"hello\": \"world\"}".getBytes(StandardCharsets.UTF_8));

        // Act
        final SimpleDto simpleDto = JsonUtils.fromJson(jsonAsInputStream, SIMPLE_DTO_JAVA_TYPE);

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
            () -> JsonUtils.fromJson(jsonAsByteArray, SIMPLE_DTO_CLASS_TYPE));

        // Assert
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("From JSON conversion failure", exception.getMessage());
    }

    @Test
    void fromJsonString() {

        // Arrange
        final String jsonAsString = "{\"hello\": \"world\"}";

        // Act
        final SimpleDto simpleDto = JsonUtils.fromJson(jsonAsString, SIMPLE_DTO_CLASS_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world", simpleDto.hello);
    }

    @Test
    void fromJsonStringJavaType() {

        // Arrange
        final String jsonAsString = "{\"hello\": \"world\"}";

        // Act
        final SimpleDto simpleDto = JsonUtils.fromJson(jsonAsString, SIMPLE_DTO_JAVA_TYPE);

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
            () -> JsonUtils.fromJson(jsonAsString, SIMPLE_DTO_CLASS_TYPE));

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
        final SimpleDto simpleDto = JsonUtils.fromJson(objectNode, SIMPLE_DTO_CLASS_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world", simpleDto.hello);
    }

    @Test
    void fromJsonJsonNodeJavaType() {

        // Arrange
        final ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("hello", "world");

        // Act
        final SimpleDto simpleDto = JsonUtils.fromJson(objectNode, SIMPLE_DTO_JAVA_TYPE);

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
        final SimpleDto simpleDto = JsonUtils.fromMap(dataMap, SIMPLE_DTO_CLASS_TYPE);

        // Assert
        Assertions.assertNotNull(simpleDto);
        Assertions.assertEquals("world!", simpleDto.hello);
    }

    @Test
    void fromMapJavaType() {

        // Arrange
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("hello", "world!");

        // Act
        final SimpleDto simpleDto = JsonUtils.fromMap(dataMap, SIMPLE_DTO_JAVA_TYPE);

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
    public record SimpleDto(String hello) {

        @JsonCreator
        public SimpleDto(@JsonProperty("hello") final String hello) {

            this.hello = hello;
        }
    }
}
