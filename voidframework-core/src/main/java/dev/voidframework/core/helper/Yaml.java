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

    public static final ObjectMapper objectMapper = JsonMapper.builder(new YAMLFactory())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
        .addModule(new JavaTimeModule())
        .addModule(new Jdk8Module())
        .addModule(new JodaModule())
        .build();

    /**
     * Converts an YAML to string.
     *
     * @param yaml The YAML to convert.
     * @return The string representation.
     */
    public static String toString(final JsonNode yaml) {
        try {
            final ObjectWriter writer = objectMapper.writer();
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
    public static JsonNode toYaml(final Object obj) {
        try {
            return objectMapper.valueToTree(obj);
        } catch (final IllegalArgumentException e) {
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
            return objectMapper.readTree(data);
        } catch (final IllegalArgumentException | IOException e) {
            return null;
        }
    }

    /**
     * Converts a YAML document into to a Java object.
     *
     * @param <OUTPUT_TYPE> The type of the Java object
     * @param yaml          YAML document to convert
     * @param clazz         Expected Java object type
     * @return The Java object
     */
    public static <OUTPUT_TYPE> OUTPUT_TYPE fromYaml(final JsonNode yaml, final Class<OUTPUT_TYPE> clazz) {
        try {
            return objectMapper.treeToValue(yaml, clazz);
        } catch (final NullPointerException | IllegalArgumentException | JsonProcessingException e) {
            return null;
        }
    }
}
