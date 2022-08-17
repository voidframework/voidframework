package dev.voidframework.core.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.Map;

/**
 * Helper to handle JSON document.
 */
public final class Json {

    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {
    };

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false).addModule(new JavaTimeModule()).addModule(new Jdk8Module()).addModule(new JodaModule()).build();

    /**
     * Default constructor.
     */
    private Json() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts a JSON to string.
     *
     * @param json The JSON to convert.
     * @return The string representation.
     */
    public static String toString(final JsonNode json) {

        try {
            final ObjectWriter writer = OBJECT_MAPPER.writer();
            return writer.writeValueAsString(json);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts an object to JSON document.
     *
     * @param obj Object to convert in JSON
     * @return The JSON node
     */
    public static JsonNode toJson(final Object obj) {

        try {
            return OBJECT_MAPPER.valueToTree(obj);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a byte array to a JSON document.
     *
     * @param data Data to convert in JSON
     * @return The JSON node
     */
    public static JsonNode toJson(final byte[] data) {

        try {
            return OBJECT_MAPPER.readTree(data);
        } catch (final IllegalArgumentException | IOException ignore) {
            return null;
        }
    }

    /**
     * Converts a JSON document into to a Java object.
     *
     * @param <T>             The type of the Java object
     * @param json            JSON document to convert
     * @param outputClassType Expected Java object type
     * @return The Java object
     */
    public static <T> T fromJson(final JsonNode json, final Class<T> outputClassType) {

        try {
            return OBJECT_MAPPER.treeToValue(json, outputClassType);
        } catch (final NullPointerException | IllegalArgumentException | JsonProcessingException ignore) {
            return null;
        }
    }

    /**
     * Converts a JSON document into to a Java object.
     *
     * @param <T>             The type of the Java object
     * @param jsonByteArray   JSON document as bytes array to convert
     * @param outputClassType Expected Java object type
     * @return The Java object
     */
    public static <T> T fromJson(final byte[] jsonByteArray, final Class<T> outputClassType) {

        try {
            return OBJECT_MAPPER.readValue(jsonByteArray, outputClassType);
        } catch (final NullPointerException | IOException | IllegalArgumentException ignore) {
            return null;
        }
    }

    /**
     * Converts a JSON document into to a Java object.
     *
     * @param <T>             The type of the Java object
     * @param json            JSON document as String to convert
     * @param outputClassType Expected Java object type
     * @return The Java object
     */
    public static <T> T fromJson(final String json, final Class<T> outputClassType) {

        try {
            return OBJECT_MAPPER.readValue(json, outputClassType);
        } catch (final NullPointerException | IOException | IllegalArgumentException ignore) {
            return null;
        }
    }

    /**
     * Converts a data map into to a Java object.
     *
     * @param <T>             The type of the Java object
     * @param dataMap         Data map to convert
     * @param outputClassType Expected Java object type
     * @return The Java object
     */
    public static <T> T fromMap(final Map<?, ?> dataMap, final Class<T> outputClassType) {

        try {
            return OBJECT_MAPPER.convertValue(dataMap, outputClassType);
        } catch (final NullPointerException | IllegalArgumentException ignore) {
            return null;
        }
    }

    /**
     * Converts an object into a data map.
     *
     * @param obj Object to convert
     * @return The data map
     */
    public static Map<String, Object> toMap(final Object obj) {

        try {
            return OBJECT_MAPPER.convertValue(obj, MAP_TYPE_REFERENCE);
        } catch (final NullPointerException | IllegalArgumentException ignore) {
            return null;
        }
    }

    /**
     * Gets the object mapper.
     *
     * @return JSON object mapper
     */
    public static ObjectMapper objectMapper() {

        return OBJECT_MAPPER;
    }
}
