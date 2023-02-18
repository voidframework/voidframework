package dev.voidframework.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.voidframework.core.exception.JsonException;
import dev.voidframework.core.jackson.VoidFrameworkModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Utility to handle JSON document.
 *
 * @since 1.0.0
 */
public final class JsonUtils {

    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {
    };

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
        .addModule(new JavaTimeModule())
        .addModule(new Jdk8Module())
        .addModule(new JodaModule())
        .addModule(new VoidFrameworkModule())
        .build();

    /**
     * Default constructor.
     *
     * @since 1.0.0
     */
    private JsonUtils() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts a JSON to string.
     *
     * @param json The JSON to convert.
     * @return The string representation.
     * @since 1.0.0
     */
    public static String toString(final JsonNode json) {

        try {
            final ObjectWriter writer = OBJECT_MAPPER.writer();
            return writer.writeValueAsString(json);
        } catch (final IOException ex) {
            throw new JsonException.ToStringConversionFailure(ex);
        }
    }

    /**
     * Converts a JSON to string.
     *
     * @param obj Object to convert in JSON.
     * @return The string representation.
     * @since 1.0.0
     */
    public static String toString(final Object obj) {

        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (final IOException ex) {
            throw new JsonException.ToStringConversionFailure(ex);
        }
    }

    /**
     * Converts an object to JSON document.
     *
     * @param obj Object to convert in JSON
     * @return The JSON node
     * @since 1.0.0
     */
    public static JsonNode toJson(final Object obj) {

        try {
            return OBJECT_MAPPER.valueToTree(obj);
        } catch (final Exception ex) {
            throw new JsonException.ToJsonConversionFailure(ex);
        }
    }

    /**
     * Converts an {@code InputStream} to JSON document.
     *
     * @param inputStreamJson {@code InputStream} containing data to convert in JSON
     * @return The JSON node
     * @since 1.3.0
     */
    public static JsonNode toJson(final InputStream inputStreamJson) {

        if (inputStreamJson == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readTree(inputStreamJson);
        } catch (final Exception ex) {
            throw new JsonException.ToJsonConversionFailure(ex);
        }
    }

    /**
     * Converts a byte array to a JSON document.
     *
     * @param data Data to convert in JSON
     * @return The JSON node
     * @since 1.0.0
     */
    public static JsonNode toJson(final byte[] data) {

        try {
            return OBJECT_MAPPER.readTree(data);
        } catch (final Exception ex) {
            throw new JsonException.ToJsonConversionFailure(ex);
        }
    }

    /**
     * Converts a JSON document into to a Java object.
     *
     * @param <T>             The type of the Java object
     * @param json            JSON document to convert
     * @param outputClassType Expected Java object type
     * @return The Java object
     * @since 1.0.0
     */
    public static <T> T fromJson(final JsonNode json, final Class<T> outputClassType) {

        try {
            return OBJECT_MAPPER.treeToValue(json, outputClassType);
        } catch (final NullPointerException | IllegalArgumentException | JsonProcessingException ex) {
            throw new JsonException.FromJsonConversionFailure(ex);
        }
    }

    /**
     * Converts a JSON document into to a Java object.
     *
     * @param <T>            The type of the Java object
     * @param json           JSON document to convert
     * @param outputJavaType Expected Java object type
     * @return The Java object
     * @since 1.3.0
     */
    public static <T> T fromJson(final JsonNode json, final JavaType outputJavaType) {

        try {
            return OBJECT_MAPPER.treeToValue(json, outputJavaType);
        } catch (final NullPointerException | IllegalArgumentException | JsonProcessingException ex) {
            throw new JsonException.FromJsonConversionFailure(ex);
        }
    }

    /**
     * Converts a JSON document into to a Java object.
     *
     * @param <T>             The type of the Java object
     * @param jsonByteArray   JSON document as bytes array to convert
     * @param outputClassType Expected Java object type
     * @return The Java object
     * @since 1.0.1
     */
    public static <T> T fromJson(final byte[] jsonByteArray, final Class<T> outputClassType) {

        if (jsonByteArray == null || jsonByteArray.length == 0) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(jsonByteArray, outputClassType);
        } catch (final NullPointerException | IOException | IllegalArgumentException ex) {
            throw new JsonException.FromJsonConversionFailure(ex);
        }
    }

    /**
     * Converts a JSON document into to a Java object.
     *
     * @param <T>             The type of the Java object
     * @param inputStreamJson {@code InputStream} containing a JSON document to convert
     * @param outputClassType Expected Java object type
     * @return The Java object
     * @since 1.3.0
     */
    public static <T> T fromJson(final InputStream inputStreamJson, final Class<T> outputClassType) {

        if (inputStreamJson == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(inputStreamJson, outputClassType);
        } catch (final NullPointerException | IOException | IllegalArgumentException ex) {
            throw new JsonException.FromJsonConversionFailure(ex);
        }
    }

    /**
     * Converts a JSON document into to a Java object.
     *
     * @param <T>            The type of the Java object
     * @param jsonByteArray  JSON document as bytes array to convert
     * @param outputJavaType Expected Java object type
     * @return The Java object
     * @since 1.3.0
     */
    public static <T> T fromJson(final byte[] jsonByteArray, final JavaType outputJavaType) {

        if (jsonByteArray == null || jsonByteArray.length == 0) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(jsonByteArray, outputJavaType);
        } catch (final NullPointerException | IOException | IllegalArgumentException ex) {
            throw new JsonException.FromJsonConversionFailure(ex);
        }
    }

    /**
     * Converts a JSON document into to a Java object.
     *
     * @param <T>             The type of the Java object
     * @param inputStreamJson {@code InputStream} containing a JSON document to convert
     * @param outputJavaType  Expected Java object type
     * @return The Java object
     * @since 1.3.0
     */
    public static <T> T fromJson(final InputStream inputStreamJson, final JavaType outputJavaType) {

        if (inputStreamJson == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(inputStreamJson, outputJavaType);
        } catch (final NullPointerException | IOException | IllegalArgumentException ex) {
            throw new JsonException.FromJsonConversionFailure(ex);
        }
    }

    /**
     * Converts a JSON document into to a Java object.
     *
     * @param <T>             The type of the Java object
     * @param json            JSON document as String to convert
     * @param outputClassType Expected Java object type
     * @return The Java object
     * @since 1.2.0
     */
    public static <T> T fromJson(final String json, final Class<T> outputClassType) {

        try {
            return OBJECT_MAPPER.readValue(json, outputClassType);
        } catch (final NullPointerException | IOException | IllegalArgumentException ex) {
            throw new JsonException.FromJsonConversionFailure(ex);
        }
    }

    /**
     * Converts a JSON document into to a Java object.
     *
     * @param <T>            The type of the Java object
     * @param json           JSON document as String to convert
     * @param outputJavaType Expected Java object type
     * @return The Java object
     * @since 1.3.0
     */
    public static <T> T fromJson(final String json, final JavaType outputJavaType) {

        try {
            return OBJECT_MAPPER.readValue(json, outputJavaType);
        } catch (final NullPointerException | IOException | IllegalArgumentException ex) {
            throw new JsonException.FromJsonConversionFailure(ex);
        }
    }

    /**
     * Converts a data map into to a Java object.
     *
     * @param <T>             The type of the Java object
     * @param dataMap         Data map to convert
     * @param outputClassType Expected Java object type
     * @return The Java object
     * @since 1.0.0
     */
    public static <T> T fromMap(final Map<?, ?> dataMap, final Class<T> outputClassType) {

        try {
            return OBJECT_MAPPER.convertValue(dataMap, outputClassType);
        } catch (final NullPointerException | IllegalArgumentException ignore) {
            return null;
        }
    }

    /**
     * Converts a data map into to a Java object.
     *
     * @param <T>            The type of the Java object
     * @param dataMap        Data map to convert
     * @param outputJavaType Expected Java object type
     * @return The Java object
     * @since 1.3.0
     */
    public static <T> T fromMap(final Map<?, ?> dataMap, final JavaType outputJavaType) {

        try {
            return OBJECT_MAPPER.convertValue(dataMap, outputJavaType);
        } catch (final NullPointerException | IllegalArgumentException ignore) {
            return null;
        }
    }

    /**
     * Converts an object into a data map.
     *
     * @param obj Object to convert
     * @return The data map
     * @since 1.0.0
     */
    @SuppressWarnings("java:S1168")
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
     * @since 1.1.0
     */
    public static ObjectMapper objectMapper() {

        return OBJECT_MAPPER;
    }
}
