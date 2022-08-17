package dev.voidframework.core.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * Helper to handle YAML document.
 */
public final class Yaml {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder(new YAMLFactory())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
        .addModule(new JavaTimeModule())
        .addModule(new Jdk8Module())
        .addModule(new JodaModule())
        .build();

    /**
     * Default constructor.
     */
    private Yaml() {

        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts an YAML to string.
     *
     * @param yaml The YAML to convert.
     * @return The string representation.
     */
    public static String toString(final JsonNode yaml) {

        try {
            final ObjectWriter writer = OBJECT_MAPPER.writer();
            return writer.writeValueAsString(yaml);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts an object to YAML document.
     *
     * @param obj Object to convert in YAML
     * @return The YAML node
     */
    public static String toString(final Object obj) {

        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (final IllegalArgumentException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a byte array to a YAML document.
     *
     * @param data data to convert in YAML
     * @return The YAML node
     */
    public static JsonNode toYaml(final byte[] data) {

        try {
            return OBJECT_MAPPER.readTree(data);
        } catch (final IllegalArgumentException | IOException ignore) {
            return null;
        }
    }

    /**
     * Converts a YAML document into to a Java object.
     *
     * @param <T>             The type of the Java object
     * @param yaml            YAML document to convert
     * @param outputClassType Expected Java object type
     * @return The Java object
     */
    public static <T> T fromYaml(final JsonNode yaml, final Class<T> outputClassType) {

        try {
            return OBJECT_MAPPER.treeToValue(yaml, outputClassType);
        } catch (final NullPointerException | IllegalArgumentException | JsonProcessingException ignore) {
            return null;
        }
    }

    /**
     * Converts a YAML document into to a Java object.
     *
     * @param <T>             The type of the Java object
     * @param yamlByteArray   YAML document as bytes array to convert
     * @param outputClassType Expected Java object type
     * @return The Java object
     */
    public static <T> T fromYaml(final byte[] yamlByteArray, final Class<T> outputClassType) {

        try {
            return OBJECT_MAPPER.readValue(yamlByteArray, outputClassType);
        } catch (final NullPointerException | IllegalArgumentException | IOException ignore) {
            return null;
        }
    }
}
